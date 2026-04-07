package cz.vitezslav.xmlimport.repository;

import cz.vitezslav.xmlimport.domain.CastObceRecord;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CastObceRepository {

    private static final String UPSERT_SQL = """
            INSERT INTO cast_obce (kod, nazev, kod_obce) VALUES (?, ?, ?)
            ON CONFLICT (kod) DO UPDATE SET
                nazev = EXCLUDED.nazev,
                kod_obce = EXCLUDED.kod_obce
            """;

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public void replaceAllForObec(String kodObce, List<CastObceRecord> castiObce) {
        if (castiObce.isEmpty()) {
            namedJdbcTemplate.update(
                    "DELETE FROM cast_obce WHERE kod_obce = :kodObce",
                    new MapSqlParameterSource("kodObce", kodObce)
            );
            return;
        }

        namedJdbcTemplate.getJdbcTemplate().batchUpdate(
                UPSERT_SQL,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        CastObceRecord cast = castiObce.get(i);
                        ps.setString(1, cast.kod());
                        ps.setString(2, cast.nazev());
                        ps.setString(3, cast.kodObce());
                    }

                    @Override
                    public int getBatchSize() {
                        return castiObce.size();
                    }
                }
        );

        List<String> kody = castiObce.stream().map(CastObceRecord::kod).toList();
        namedJdbcTemplate.update(
                "DELETE FROM cast_obce WHERE kod_obce = :kodObce AND kod NOT IN (:kody)",
                new MapSqlParameterSource()
                        .addValue("kodObce", kodObce)
                        .addValue("kody", kody)
        );
    }
}

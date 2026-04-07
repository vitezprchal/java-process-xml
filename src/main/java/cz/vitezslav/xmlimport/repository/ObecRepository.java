package cz.vitezslav.xmlimport.repository;

import cz.vitezslav.xmlimport.domain.ObecRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ObecRepository {

    private final JdbcTemplate jdbcTemplate;

    public void upsert(ObecRecord obec) {
        jdbcTemplate.update(
                """
                INSERT INTO obec (kod, nazev)
                VALUES (?, ?)
                ON CONFLICT (kod) DO UPDATE SET nazev = EXCLUDED.nazev
                """,
                obec.kod(),
                obec.nazev()
        );
    }
}

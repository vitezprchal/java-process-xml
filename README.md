# XML -> PostgreSQL importer (Spring Boot)

Jednoducha Java aplikace, ktera nacte lokalni RUIAN XML, vyparsuje:

- `vf:Obec` -> `kod`, `nazev`
- `vf:CastObce` -> `kod`, `nazev`, `kod_obce`

a ulozi data do PostgreSQL.

## 0) Stažení XML do `assets/`

XML soubor před spuštěním musíte mít lokálně v /assets

## 1) Start PostgreSQL

```bash
docker compose up -d
```

## 2) Vytvoreni DB schematu

```bash
psql "host=localhost port=5432 dbname=ruian user=ruian password=ruian" -f db/schema.sql
```

## 3) Spusteni aplikace

```bash
mvn spring-boot:run
```

## Konfigurace

Vychozi konfigurace je v `src/main/resources/application.yml`:

- DB: `jdbc:postgresql://localhost:5432/ruian`
- XML soubor: `assets/ruian.xml`

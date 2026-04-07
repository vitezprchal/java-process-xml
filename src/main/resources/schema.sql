CREATE TABLE IF NOT EXISTS obec (
    kod     VARCHAR(32)  PRIMARY KEY,
    nazev   VARCHAR(512) NOT NULL
);

CREATE TABLE IF NOT EXISTS cast_obce (
    kod      VARCHAR(32)  PRIMARY KEY,
    nazev    VARCHAR(512) NOT NULL,
    kod_obce VARCHAR(32)  NOT NULL REFERENCES obec (kod)
);

CREATE INDEX IF NOT EXISTS idx_cast_obce_kod_obce ON cast_obce (kod_obce);

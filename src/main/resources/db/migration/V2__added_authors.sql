CREATE TABLE authors
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    firstname  VARCHAR(128) NOT NULL,
    lastname   VARCHAR(128) NOT NULL,
    birthdate  DATE         NOT NULL,
    death_date DATE
);

ALTER TABLE books DROP COLUMN author;
ALTER TABLE books ADD COLUMN author UUID,
    ADD COLUMN publication_date DATE;
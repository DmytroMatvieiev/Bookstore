ALTER TABLE books
    RENAME COLUMN "uuid" TO id;

ALTER TABLE books
    RENAME COLUMN author TO author_id;
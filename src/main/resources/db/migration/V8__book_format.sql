CREATE TABLE book_files
(
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    book_id   UUID REFERENCES books (id),
    format    VARCHAR(10),
    file_path TEXT
);
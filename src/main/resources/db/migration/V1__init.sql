CREATE EXTENSION IF NOT EXISTS "pgcrypto";

Create TABLE books
(
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ISBN VARCHAR(13),
    title VARCHAR(256),
    author VARCHAR(256),
    pages integer
)
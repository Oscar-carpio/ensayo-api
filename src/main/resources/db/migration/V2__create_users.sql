-- Las canciones de prueba creadas antes de existir usuarios no tienen dueño.
-- Todavía no hay datos reales, así que se borran.
DELETE FROM songs;

CREATE TABLE users (
                       id            UUID         PRIMARY KEY,
                       email         VARCHAR(254) NOT NULL UNIQUE,
                       password_hash VARCHAR(100) NOT NULL,
                       created_at    BIGINT       NOT NULL
);

ALTER TABLE songs ADD COLUMN owner_id UUID NOT NULL REFERENCES users (id);

CREATE INDEX idx_songs_owner_id ON songs (owner_id);
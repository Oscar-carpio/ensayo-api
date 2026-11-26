CREATE TABLE songs (
                       id          UUID         PRIMARY KEY,
                       title       VARCHAR(200) NOT NULL,
                       artist      VARCHAR(200),
                       musical_key VARCHAR(10)  NOT NULL,
                       content     TEXT         NOT NULL,
                       created_at  BIGINT       NOT NULL,
                       updated_at  BIGINT       NOT NULL,
                       deleted     BOOLEAN      NOT NULL DEFAULT FALSE
);
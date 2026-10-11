-- Runs once, the first time the postgres volume is created.
-- Schema matches the JPA entities in rw.ac.suggestionbox.domain; ddl-auto=update
-- will simply find the tables already in place.

CREATE TABLE IF NOT EXISTS categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(60)  NOT NULL UNIQUE,
    description VARCHAR(255),
    active      BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS suggestions (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(120)  NOT NULL,
    body            VARCHAR(1000) NOT NULL,
    submitter_email VARCHAR(120)  NOT NULL,
    status          VARCHAR(20)   NOT NULL,
    created_at      TIMESTAMP     NOT NULL,
    category_id     BIGINT        NOT NULL REFERENCES categories (id)
);

INSERT INTO categories (name, description, active) VALUES
    ('Library Services', 'Opening hours, books, study space', TRUE),
    ('Cafeteria',        'Meals, pricing, hygiene',           TRUE),
    ('ICT and Network',  'Wi-Fi, labs, e-learning platform',  TRUE)
ON CONFLICT (name) DO NOTHING;

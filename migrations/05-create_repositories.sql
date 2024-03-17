CREATE TABLE repositories
(
    repository_id     SERIAL PRIMARY KEY,
    link_id           UUID UNIQUE REFERENCES links (link_id),
    subscribers_count int NOT NULL
);

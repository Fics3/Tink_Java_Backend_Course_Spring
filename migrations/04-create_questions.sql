CREATE TABLE questions
(
    question_id  SERIAL PRIMARY KEY,
    link_id      UUID UNIQUE REFERENCES links (link_id),
    answer_count int NOT NULL
);

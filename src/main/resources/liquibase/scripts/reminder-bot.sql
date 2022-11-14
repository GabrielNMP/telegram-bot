-- liquibase formatted sql

-- changeset gabriel:1
CREATE TABLE reminder
(
    id            SERIAL NOT NULL PRIMARY KEY,
    chat_id       BIGINT NOT NULL,
    reminder_text TEXT NOT NULL,
    date_time     TIMESTAMP NOT NULL
);
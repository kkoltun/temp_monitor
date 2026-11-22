--liquibase formatted sql

--changeset temp_monitor:003-add-reading-table
CREATE TABLE reading
(
    id                INTEGER PRIMARY KEY,
    source_message_id INTEGER   NOT NULL,
    value             REAL      NOT NULL,
    unit              TEXT      NOT NULL,
    location          TEXT      NOT NULL,
    stamp             TIMESTAMP NOT NULL,
    type              TEXT      NOT NULL
);

--rollback DROP TABLE reading;
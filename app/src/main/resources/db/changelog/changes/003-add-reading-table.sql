--liquibase formatted sql

--changeset temp_monitor:003-add-reading-table
CREATE TABLE reading
(
    id                BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    source_message_id BIGINT                      NOT NULL,
    value             FLOAT                       NOT NULL,
    unit              TEXT                        NOT NULL,
    location          TEXT                        NOT NULL,
    stamp             TIMESTAMP WITH TIME ZONE NOT NULL,
    type              TEXT                        NOT NULL
);

--rollback DROP TABLE reading;
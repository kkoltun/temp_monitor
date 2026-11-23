--liquibase formatted sql

--changeset temp_monitor:001-measurement
CREATE TABLE mqtt_message
(
    id      BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    topic   TEXT                     NOT NULL,
    device  TEXT                     NOT NULL,
    headers TEXT                     NOT NULL,
    payload TEXT                     NOT NULL,
    created TIMESTAMP WITH TIME ZONE NOT NULL
);

--rollback DROP TABLE mqtt_message;
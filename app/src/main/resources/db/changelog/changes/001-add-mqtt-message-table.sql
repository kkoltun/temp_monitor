--liquibase formatted sql

--changeset temp_monitor:001-measurement
CREATE TABLE mqtt_message
(
    id      INTEGER PRIMARY KEY,
    topic   TEXT      NOT NULL,
    device  TEXT      NOT NULL,
    headers TEXT      NOT NULL,
    payload TEXT      NOT NULL,
    created TIMESTAMP NOT NULL
);

--rollback DROP TABLE mqtt_message;
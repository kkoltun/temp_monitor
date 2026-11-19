--liquibase formatted sql

--changeset temp_monitor:001-measurement
CREATE TABLE measurement
(
    id      INTEGER PRIMARY KEY,
    topic   TEXT      NOT NULL,
    sensor  TEXT      NOT NULL,
    payload TEXT      NOT NULL,
    created TIMESTAMP NOT NULL
);

--rollback DROP TABLE measurement;
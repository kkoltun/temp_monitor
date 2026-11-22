--liquibase formatted sql

--changeset temp_monitor:002-add-device-location-table
CREATE TABLE device_location
(
    id       INTEGER PRIMARY KEY,
    device   TEXT NOT NULL,
    location TEXT NOT NULL,
    UNIQUE (device)
);

--rollback DROP TABLE device_location;
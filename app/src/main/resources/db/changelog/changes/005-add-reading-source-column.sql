--liquibase formatted sql

--changeset temp_monitor:005-add-reading-source-column
ALTER TABLE reading
    ADD COLUMN source TEXT NOT NULL DEFAULT 'MQTT_MESSAGE';

ALTER TABLE reading
    ALTER COLUMN source DROP DEFAULT;

--rollback ALTER TABLE reading DROP COLUMN source;

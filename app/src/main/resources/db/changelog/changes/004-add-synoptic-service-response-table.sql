--liquibase formatted sql

--changeset temp_monitor:004-synoptic-service-response
CREATE TABLE synoptic_service_response
(
    id      BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    payload TEXT                     NOT NULL,
    created TIMESTAMP WITH TIME ZONE NOT NULL
);

--rollback DROP TABLE synoptic_service_response;
create schema if not exists ping;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
create table if not exists ip_model(
                                       id  UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
                                       ip_address  VARCHAR(255) not null unique ,
                                       ip_group  varchar(255),
                                       is_enabled boolean,
                                       updated_by varchar(255),
                                       created_by varchar(255),
                                       created_at TIMESTAMPTZ,
                                       updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS ping_result (
                                           id varchar(255),
                                           ip_address VARCHAR(255) NOT NULL REFERENCES ip_model(ip_address),
                                           ping_start_time TIMESTAMPTZ,
                                           ping_end_time TIMESTAMPTZ,
                                           ping_success BOOLEAN, -- Indicating whether the ping was successful or not
                                           latency_ms DOUBLE PRECISION, -- Storing latency in milliseconds
                                           packet_loss_rate DOUBLE PRECISION, -- Packet loss rate as a percentage
                                           additional_info JSONB, -- Any additional information you may want to store
                                           updated_by varchar(255),
                                           created_by varchar(255),
                                           created_at TIMESTAMPTZ,
                                           updated_at TIMESTAMPTZ,
                                           PRIMARY KEY (id, created_at) -- Include created_at in the primary key
) PARTITION BY RANGE (created_at);

CREATE INDEX IF NOT EXISTS idx_created_at ON ping_result(created_at);

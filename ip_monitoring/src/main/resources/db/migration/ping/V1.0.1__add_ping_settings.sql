CREATE INDEX IF NOT EXISTS idx_latency ON ping.ping_result(latency_ms);
CREATE INDEX IF NOT EXISTS idx_ip_address ON ping.ping_result(ip_address);

create table if not exists ping_settings(
                                            id  UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
                                            name varchar,
                                            count integer,
                                            interval double precision,
                                            packet_size integer,
                                            os_type varchar default 'LINUX',
                                            quiet_output boolean default true,
                                            is_enabled boolean default false,
                                            updated_by varchar(255),
                                            created_by varchar(255),
                                            created_at TIMESTAMPTZ,
                                            updated_at TIMESTAMPTZ
)
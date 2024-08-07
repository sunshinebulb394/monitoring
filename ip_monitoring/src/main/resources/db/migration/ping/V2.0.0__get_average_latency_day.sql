CREATE OR REPLACE FUNCTION get_average_latency_by_day(
    IN start_date TIMESTAMP,
    IN end_date TIMESTAMP
)
RETURNS TABLE(hour numeric,avg_value numeric)
AS $$
BEGIN
    RETURN QUERY
        WITH hours AS (
            SELECT generate_series(
                           start_date,
                           end_date,
                           '1 hour'
                   ) AS hour
        )
        SELECT
            extract(hour from hours.hour) AS hour,
            COALESCE(ROUND(AVG(pr.latency_ms)::NUMERIC, 2), 0) AS avg_value
        FROM
            hours
                LEFT JOIN
            ping.ping_result pr
            ON DATE_TRUNC('hour', pr.created_at) = hours.hour
        GROUP BY
            hours.hour
        ORDER BY
            hour;
END;
$$ LANGUAGE plpgsql;

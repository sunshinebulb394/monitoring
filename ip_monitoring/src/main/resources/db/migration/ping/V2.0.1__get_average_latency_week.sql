CREATE OR REPLACE FUNCTION get_average_latency_by_week(
    IN start_date TIMESTAMP,
    IN end_date TIMESTAMP
)
    RETURNS TABLE(each_day date,day_name text,day_number numeric,avg_value numeric)
AS $$
BEGIN
    RETURN QUERY
        WITH days AS (
            SELECT generate_series(
                           start_date::DATE,  -- Start date
                           end_date::DATE,  -- End date
                           '1 day'::INTERVAL    -- Interval
                   ) AS day
        )
        SELECT days.day::DATE AS each_day,
               to_char(days.day, 'Day') AS day_name,
               EXTRACT(ISODOW FROM days.day) AS day_number,
               COALESCE(ROUND(AVG(pr.latency_ms)::NUMERIC, 2), 0) AS avg_value
        FROM days left join ping.ping_result pr on pr.created_at::DATE = days.day::DATE
        group by days.day
        order by day_number;
END;
$$ LANGUAGE plpgsql;

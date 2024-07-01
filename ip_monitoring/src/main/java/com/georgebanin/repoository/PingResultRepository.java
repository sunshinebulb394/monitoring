package com.georgebanin.repoository;

import com.georgebanin.exceptions.PingResultException;
import com.georgebanin.model.PingResult;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.*;

@ApplicationScoped
@Slf4j
public class PingResultRepository {

    @Inject
    @Named("pingSqlClient")
    SqlClient pingSqlClient;


    public void save(PingResult pingResult) {
        var tuple = convertToTuple(pingResult);

        pingSqlClient.preparedQuery("INSERT INTO ping.ping_result (id, ip_address, ping_start_time, ping_end_time, ping_success, latency_ms, packet_loss_rate, additional_info, created_by, created_at) " +
                        "VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10)")
                .execute(tuple)
                .onFailure()
                .retry()
                .atMost(2)
                .onFailure()
                .transform(Unchecked.function(throwable -> {
                    throw new PingResultException(throwable.getMessage());
                }))
                .subscribe().asCompletionStage();



    }


    public void saveAll(List<PingResult> pingResults) {
        List<Tuple> batch = new ArrayList<>();
        pingResults.parallelStream().map(this::convertToTuple).forEach(batch::add);


        pingSqlClient.preparedQuery("INSERT INTO ping.ping_result (id, ip_address, ping_start_time, ping_end_time, ping_success, latency_ms, packet_loss_rate, additional_info, created_by, created_at) " +
                        "VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10)")
                .executeBatch(batch)
                .onFailure()
                .retry()
                .atMost(2)
                .onFailure()
                .transform(Unchecked.function(throwable -> {
                    throw new PingResultException(throwable.getMessage());
                }))
                .subscribe().asCompletionStage();

    }

    public void saveAll(Set<PingResult> pingResults) {
        List<Tuple> batch = new ArrayList<>();
        pingResults.stream().map(this::convertToTuple).forEach(batch::add);


        pingSqlClient.preparedQuery("INSERT INTO ping.ping_result (id, ip_address, ping_start_time, ping_end_time, ping_success, latency_ms, packet_loss_rate, additional_info, created_by, created_at) " +
                        "VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10)")
                .executeBatch(batch)
                .onFailure()
                .retry()
                .atMost(2)
                .onFailure()
                .transform(Unchecked.function(throwable -> {
                    throw new PingResultException(throwable.getMessage());
                }))
                .subscribe().asCompletionStage();

    }

    private Tuple convertToTuple(PingResult pingResult) {
        Tuple tuple = Tuple.tuple();
        tuple.addString(pingResult.getId().toString());
        tuple.addString(pingResult.getIpAddress());
        tuple.addOffsetDateTime(pingResult.getPingStartTime());
        tuple.addOffsetDateTime(pingResult.getPingEndTime());
        tuple.addBoolean(pingResult.getPingSuccess());
        tuple.addDouble(pingResult.getLatency());
        tuple.addDouble(pingResult.getPacketLossRate());
        tuple.addJsonObject(pingResult.getAdditionalInfo());
        tuple.addString(pingResult.getCreatedBy());
        tuple.addOffsetDateTime(OffsetDateTime.now());
        return tuple;
    }


    public Multi<HashMap<String,Object>> getAverageLatencyPerDay(OffsetDateTime fromDate, OffsetDateTime toDate){
        Tuple tuple = Tuple.tuple();
        tuple.addOffsetDateTime(fromDate);
        tuple.addOffsetDateTime(toDate);

        return     pingSqlClient.preparedQuery("SELECT * FROM ping.get_average_latency_by_day($1,$2)")
                .execute(tuple)
                .onItem()
                .transformToMulti(RowSet::toMulti)
                .map(row -> {
                    HashMap<String, Object> map = new HashMap<>(); // Create a new HashMap for each row

                    map.put("hour", row.getInteger("hour"));
                    map.put("avg", row.getDouble("avg_value"));
                    return map;
                });
    }

    public Multi<HashMap<String,Object>> getAverageLatencyPerWeek(OffsetDateTime fromDate, OffsetDateTime toDate){
        Tuple tuple = Tuple.tuple();
        tuple.addOffsetDateTime(fromDate);
        tuple.addOffsetDateTime(toDate);

        return     pingSqlClient.preparedQuery("SELECT * FROM ping.get_average_latency_by_week($1,$2)")
                .execute(tuple)
                .onItem()
                .transformToMulti(RowSet::toMulti)
                .map(row -> {
                    HashMap<String, Object> map = new HashMap<>(); // Create a new HashMap for each row

                    map.put("eachDay", row.getLocalDate("each_day"));
                    map.put("avg", row.getDouble("avg_value"));
                    map.put("dayNumber", row.getInteger("day_number"));
                    map.put("dayName", row.getString("day_name"));

                    return map;
                });
    }
}

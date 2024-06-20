package com.georgebanin.repoository;

import com.georgebanin.dto.DashboardCardStatsDto;
import com.georgebanin.exceptions.IpModelException;
import com.georgebanin.exceptions.ObjectNotValidException;
import com.georgebanin.model.IpModel;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.mutiny.sqlclient.Row;
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
public class IPModelRepository {

    @Inject
    @Named("pingSqlClient")
    SqlClient pingSqlClient;


    public Uni<IpModel> findById(UUID id) {
        return pingSqlClient.preparedQuery("SELECT * FROM ping.ip_model WHERE id = $1")
                .execute(Tuple.of(id))
                .onItem()
                .transform(rowSet -> rowSet.iterator().next())
                .onItem().transform((Unchecked.function(row -> {
                    try {
                        return fetchResults(row);
                    } catch (IpModelException e) {
                        throw new RuntimeException(e);
                    }
                })));


    }

    public Multi<IpModel> findAll() {
        return pingSqlClient.query("SELECT * from ping.ip_model")
                .execute()
                .onItem()
                .transformToMulti(RowSet::toMulti)
                .map(Unchecked.function(rows -> {
                    try {
                        return fetchResults(rows);
                    } catch (IpModelException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }


    public Uni<?> save(IpModel ipModel) throws ObjectNotValidException {
        Tuple tuple = Tuple.tuple();
        tuple.addString(ipModel.getIpAddress());
        tuple.addString(ipModel.getIpGroup());
        tuple.addBoolean(true);
        tuple.addOffsetDateTime(OffsetDateTime.now());
        return pingSqlClient.preparedQuery("INSERT INTO ping.ip_model (ip_address,ip_group,is_enabled,created_at) values ($1,$2,$3,$4) RETURNING id,ip_group,ip_address,is_enabled")
                .execute(tuple)
                .onItem()
                .transform(rowSet -> rowSet.iterator().next())
                .onItem()
                .transform((Unchecked.function(row -> {
                    try {
                        return fetchResults(row);
                    } catch (IpModelException e) {
                        throw new RuntimeException(e);
                    }
                })))
                .onFailure().transform(Unchecked.function(throwable -> {
                    // Throw the exception if the SQL query execution fails
                    throw new Exception(throwable.getMessage());
                }));
    }

    private IpModel fetchResults(Row row) throws IpModelException {
        IpModel ipModel = new IpModel();

        if (row.size() == 0) {
            log.info("No rows found");
            throw new IpModelException("No record found", HttpResponseStatus.NOT_FOUND);
        }

        ipModel.setId(row.getUUID("id"));
        ipModel.setIpGroup(row.getString("ip_group"));
        ipModel.setIsEnabled(row.getBoolean("is_enabled"));
        ipModel.setIpAddress(row.getString("ip_address"));

        return ipModel;

    }

    public Multi<HashMap<String, Object>> findAllEnabledIps() {
        return pingSqlClient.query("Select id,ip_address,ip_group from ping.ip_model where is_enabled = true")
                .execute()
                .onItem()
                .transformToMulti(RowSet::toMulti)
                .map(row -> {
                    HashMap<String, Object> map = new HashMap<>(); // Create a new HashMap for each row

                    map.put("id", row.getUUID("id"));
                    map.put("ipAddress", row.getString("ip_address"));
                    map.put("ipGroup", row.getString("ip_group"));
                    return map;
                });
    }

    public Uni<List<DashboardCardStatsDto>> countIpStats() {
        return pingSqlClient.query("SELECT " +
                        "COUNT(*) AS totalIps, " +
                        "COUNT(CASE " +
                        "       WHEN is_enabled = true THEN 1 " +
                        "    END) AS enabledIps, " +
                        "COUNT(CASE " +
                        "       WHEN is_enabled = false THEN 1 " +
                        "    END) AS disabledIps " +
                        "FROM ping.ip_model;")
                .execute()
                .onItem()
                .transform(rowSet -> rowSet.iterator().next())
                .onItem()
                .transform(row -> {
                    List<DashboardCardStatsDto> dashboardCardStatsDtoList = new ArrayList<>();

                    if (row.size() == 0) {
                        dashboardCardStatsDtoList.add(new DashboardCardStatsDto(row.getColumnName(0), 0L));
                        dashboardCardStatsDtoList.add(new DashboardCardStatsDto(row.getColumnName(1), 0L));
                        dashboardCardStatsDtoList.add(new DashboardCardStatsDto(row.getColumnName(2), 0L));
                        return dashboardCardStatsDtoList;
                    }
                    dashboardCardStatsDtoList.add(new DashboardCardStatsDto(row.getColumnName(0), row.getLong(row.getColumnName(0))));
                    dashboardCardStatsDtoList.add(new DashboardCardStatsDto(row.getColumnName(1), row.getLong(row.getColumnName(1))));
                    dashboardCardStatsDtoList.add(new DashboardCardStatsDto(row.getColumnName(2), row.getLong(row.getColumnName(2))));
                    return dashboardCardStatsDtoList;

                });
    }



}
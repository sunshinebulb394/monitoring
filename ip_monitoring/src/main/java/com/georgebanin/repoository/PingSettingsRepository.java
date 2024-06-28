package com.georgebanin.repoository;

import com.georgebanin.exceptions.IpModelException;
import com.georgebanin.exceptions.PingSettingsException;
import com.georgebanin.model.PingSettings;
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

@ApplicationScoped
@Slf4j
public class PingSettingsRepository {

    @Inject
    @Named("pingSqlClient")
    SqlClient pingSqlClient;

    public Uni<PingSettings> save(PingSettings pingSettings) {

        Tuple tuple = Tuple.tuple();
        tuple.addString(pingSettings.getName());
        tuple.addInteger(pingSettings.getCount());
        tuple.addInteger(pingSettings.getPacketSize());
        tuple.addOffsetDateTime(OffsetDateTime.now());
        tuple.addString(pingSettings.getCreatedBy());

      return  pingSqlClient.preparedQuery("INSERT INTO ping.ping_settings ( name, count, packet_size, created_at, created_by) " +
                " VALUES ($1,$2,$3,$4,$5) RETURNING id,name,count,packet_size")
                .execute(tuple)
                .onFailure()
                .transform(Unchecked.function(throwable -> {
                    throw new PingSettingsException(throwable.getMessage());
                }))
                .onItem()
                .transform(rowSet -> rowSet.iterator().next())
              .map(Unchecked.function(row -> {
                  try {
                      return fetchResult(row);
                  } catch (PingSettingsException e) {
                      throw new RuntimeException(e);
                  }
              }));


    }

    public Multi<PingSettings> findAll(){
        return pingSqlClient.query("SELECT * FROM ping.ping_settings")
                .execute()
                .onItem()
                .transformToMulti(RowSet::toMulti)
                .map(Unchecked.function(row -> {
                    try {
                        return fetchResult(row);
                    } catch (PingSettingsException e) {
                        throw new RuntimeException(e);
                    }
                }));

    }

    public Uni<PingSettings> fetchByName(String name){
        return pingSqlClient.preparedQuery("SELECT * FROM ping.ping_settings WHERE name = $1")
                .execute(Tuple.of(name))
                .onItem()
                .transform(rowSet -> rowSet.iterator().next())
                .onItem().transform((Unchecked.function(row -> {
                    try {
                        return fetchResult(row);
                    } catch (PingSettingsException e) {
                        throw new RuntimeException(e);
                    }
                })));
    }


    private PingSettings fetchResult(Row row) throws PingSettingsException {
        PingSettings pingSettings = new PingSettings();

        if(row.size() == 0){
            log.info("No rows found");
            throw new PingSettingsException("No record found");
        }

        pingSettings.setId(row.getUUID("id").toString());
        pingSettings.setName(row.getString("name"));
        pingSettings.setCount(row.getInteger("count"));
        pingSettings.setPacketSize(row.getInteger("packet_size"));
        return pingSettings;
    }






}

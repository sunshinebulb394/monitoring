package com.georgebanin.migration;

import io.quarkus.runtime.StartupEvent;
import io.vertx.mutiny.sqlclient.SqlClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.exception.FlywayValidateException;

@ApplicationScoped
@Slf4j
public class FlywayMigration {

    @Inject
    @Named("quartzFlyway")
    Flyway quartzFlyway;

    @Inject
    @Named("pingFlyway")
    Flyway pingFlyway;



    void onStart(@Observes StartupEvent event){
        try {
            quartzFlyway.migrate();
        }catch (FlywayValidateException ex){
            log.error("quarkus flyway migration failed");
            log.error(ex.getMessage(),ex);
            throw new RuntimeException(ex);
        }

        try {
            pingFlyway.migrate();
        }
        catch (FlywayValidateException ex){
            log.error("ping flyway migration failed");
            log.error(ex.getMessage(),ex);
            throw new RuntimeException(ex);

        }
    }
}

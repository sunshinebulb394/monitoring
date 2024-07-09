package com.georgebanin.config;


import com.georgebanin.factory.CustomThreadFactory;
import com.maxmind.geoip2.DatabaseReader;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;


import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.pgclient.PgBuilder;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;


import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.*;

/**
 *
 * This class is used to configure beans
 */

@Singleton
@Slf4j
public class Config {

    @Inject
    @DataSource("quartz")
    AgroalDataSource quartzDataSource;

    @Inject
    DbProps dbProps;

    @Inject
    QuartzProps quartzProps;



    @Produces
    @Named("quartzFlyway")
    public Flyway quartzFLyway(){
    return  Flyway.configure()
                .dataSource(quartzDataSource)
                .locations("db/migration/quartz")
                .baselineOnMigrate(true)
                .installedBy("George Banin")
                .connectRetries(2)
                .load();
    }

    @Produces
    @Named("pingFlyway")
    public Flyway pingFlyway(){
        return Flyway.configure()
                .dataSource("jdbc:postgresql://localhost:5432/ip_analytics",dbProps.username(),dbProps.password())
                .schemas("ping")
                .locations("db/migration/ping")
                .baselineOnMigrate(true)
                .installedBy("George Banin")
                .connectRetries(2)
                .load();
    }

//    public Properties properties(){
//        Properties quartzProperties = new Properties();
//        quartzProperties.setProperty("org.quartz.scheduler.instanceName","PingScheduler");
//        quartzProperties.setProperty("org.quartz.scheduler.instanceId","PING");
//        quartzProperties.setProperty("org.quartz.threadPool.class","org.quartz.simpl.SimpleThreadPool");
//        quartzProperties.setProperty("org.quartz.threadPool.threadCount","600");
//        quartzProperties.setProperty("org.quartz.jobStore.dataSource","quartzDataSource");
//        quartzProperties.setProperty("org.quartz.jobStore.class","org.quartz.impl.jdbcjobstore.JobStoreTX");
//        quartzProperties.setProperty("org.quartz.jobStore.driverDelegateClass","org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
//        quartzProperties.setProperty("org.quartz.dataSource.quartzDataSource.driver", "org.postgresql.Driver");
//        quartzProperties.setProperty("org.quartz.dataSource.quartzDataSource.URL", "jdbc:postgresql://localhost:5432/test_quartz");
//        quartzProperties.setProperty("org.quartz.dataSource.quartzDataSource.user", "georgebanin");
//        quartzProperties.setProperty("org.quartz.dataSource.quartzDataSource.password", "wonderful143");
//
//
//        return quartzProperties;
//
//
//    }

    public Properties properties(){
        Properties quartzProperties = new Properties();
        quartzProperties.setProperty("org.quartz.scheduler.instanceName",quartzProps.scheduler().instanceName());
        quartzProperties.setProperty("org.quartz.scheduler.instanceId",quartzProps.scheduler().instanceId());
        quartzProperties.setProperty("org.quartz.threadPool.class",quartzProps.threadPool().className());
        quartzProperties.setProperty("org.quartz.threadPool.threadCount",quartzProps.threadPool().threadCount());
        quartzProperties.setProperty("org.quartz.jobStore.dataSource",quartzProps.jobStore().dataSource());
        quartzProperties.setProperty("org.quartz.jobStore.class",quartzProps.jobStore().className());
        quartzProperties.setProperty("org.quartz.jobStore.driverDelegateClass",quartzProps.jobStore().driverDelegateClass());
        quartzProperties.setProperty("org.quartz.dataSource.quartzDataSource.driver", quartzProps.dataSource().quartzDataSource().driver());
        quartzProperties.setProperty("org.quartz.dataSource.quartzDataSource.URL", quartzProps.dataSource().quartzDataSource().URL());
        quartzProperties.setProperty("org.quartz.dataSource.quartzDataSource.user", quartzProps.dataSource().quartzDataSource().user());
        quartzProperties.setProperty("org.quartz.dataSource.quartzDataSource.password", quartzProps.dataSource().quartzDataSource().password());


        return quartzProperties;


    }

    @Produces
    @Named("pingExecutor")
    public ExecutorService produceExecutorService() {
//        return Executors.newFixedThreadPool(500, new CustomThreadFactory("PING"));
        int corePoolSize = 300;  // Set your desired core pool size
        int maximumPoolSize = 400;  // Set your desired maximum pool size
        long keepAliveTime = 60L;  // Keep-alive time for idle threads
        TimeUnit unit = TimeUnit.SECONDS;

        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                new LinkedBlockingQueue<>(100),
                new CustomThreadFactory("PING")
        );
    }

    @Produces
    @Named("PingSched")
    public Scheduler scheduler() throws SchedulerException {
        log.info("Starting Quartz scheduler...");
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory(properties());
         schedulerFactory.getScheduler().start();
        log.info("Quartz scheduler successfully started");

        return schedulerFactory.getScheduler();


    }


    public PgConnectOptions pingDbOptions(){
        return new PgConnectOptions()
                .setPort(dbProps.port())
                .setHost(dbProps.host())
                .setDatabase(dbProps.db())
                .setUser(dbProps.username())
                .setPassword(dbProps.password());


    }


    public PoolOptions pingPoolOptions(){
        return new PoolOptions()
                .setMaxSize(100);

    }



    @Produces
    @Named("pingSqlClient")
    public SqlClient pingSqlClient(){
        log.info("Connecting to database...");
        var pgBuilder =  PgBuilder
                .client()
                .with(pingPoolOptions())
                .connectingTo(pingDbOptions())
                .using(Vertx.vertx())
                .build();
        log.info("Connection Successfully established!");
        return pgBuilder;

    }

    @Produces
    @Named("GeoIpReader")
    public DatabaseReader geoIpReader() throws IOException {

        DatabaseReader reader = new DatabaseReader.Builder(getClass().getClassLoader().getResourceAsStream("GeoLite2-City/GeoLite2-City.mmdb")).build();
        return reader;
    }


}

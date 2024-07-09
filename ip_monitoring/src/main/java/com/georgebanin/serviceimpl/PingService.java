package com.georgebanin.serviceimpl;

import com.georgebanin.model.IpObj;
import com.georgebanin.model.PingResult;
import com.georgebanin.repoository.IPModelRepository;
import com.georgebanin.repoository.PingResultRepository;
import com.georgebanin.repoository.PingSettingsRepository;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.*;

@ApplicationScoped
@Slf4j
public class PingService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final List<IpObj> ipObjList = Collections.synchronizedList(new ArrayList<>());
    @Inject
    IPModelRepository ipModelRepository;
    @Inject
    PingSettingsRepository pingSettingsRepository;
    @Inject
    PingResultRepository pingResultRepository;

    @Inject
    @Named("pingExecutor")
    ExecutorService pingExecutor;
    private String countSize = null;
    private String packetSize = null;

//    private List<Callable<PingResult>> pingTasks = new ArrayList<>();


    public void ping(@Observes StartupEvent ev) {
        ipModelRepository.findAllEnabledIps()
                .onItem()
                .transform(ip -> new IpObj((String) ip.get("ipAddress"), (UUID) ip.get("id")))
                .collect().asList()
                .subscribe().with(
                        list -> {
                            ipObjList.addAll(list);

                            pingSettingsRepository.fetchByName("default")
                                    .subscribe()
                                    .with(settings -> {
                                        countSize = settings.getCount().toString();
                                        packetSize = settings.getPacketSize().toString();
                                        schedulePing();

                                    });

                        },
                        failure -> log.error("Failed to load IP addresses: {}", failure.getMessage())
                );

        System.out.println(OffsetDateTime.now());

    }

    private void schedulePing() {
        scheduler.scheduleWithFixedDelay(this::pingI, 0, 30, TimeUnit.SECONDS);
    }

    private void pingI() {
        if (ipObjList.isEmpty()) {
            log.warn("IP list is empty, skipping ping.");
            return;
        }
        Multi.createFrom()
                .iterable(ipObjList)
                .onItem()
                .transform(PingTask::new)
                .collect().asList()
                .onItem()
                .transform(Unchecked.function(pingTasks -> {
                    try {
                        return pingExecutor.invokeAll(pingTasks);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .onItem()


                .transform(
                        Unchecked.function(ipObj -> {
                            try {
                                var pingr = pingExecutor.submit(new PingTask(ipObj));
                                return pingr.get();
                            } catch (InterruptedException e) {
                                log.error(e.getMessage());
                            }
                            return null;
                        }))
                .collect().asList().toMulti()
                .invoke(savePingResultList -> {
                    log.info("Saving Ping Result: {}", savePingResultList.size());
                    pingResultRepository.saveAll(savePingResultList);

                })
                .subscribe()
                .with(pingResults -> log.info("Ping results processed successfully"),
                        failure -> log.error("Failed to save Ping Result: {}", failure.getMessage(), failure.getCause())
//                        () -> {
//                            pingExecutor.shutdown();
//                            try {
//                                if (!pingExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
//                                 var tobeExec =   pingExecutor.shutdownNow();
//                                    log.info("Task waiting to be executed {}",tobeExec.size());
//
//                                    if (!pingExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
//                                        log.error("Executor did not terminate");
//                                    }
//                                }
//                            } catch (InterruptedException e) {
//                                log.error("Error during executor termination", e);
//                                pingExecutor.shutdownNow();
//                                Thread.currentThread().interrupt();
//                            }
//                            log.info("Ping executor shut down successfully");
//                        }
                );


    }




}

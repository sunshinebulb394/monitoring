package com.georgebanin.serviceimpl;

import com.georgebanin.model.IpObj;
import com.georgebanin.repoository.IPModelRepository;
import com.georgebanin.repoository.PingResultRepository;
import com.georgebanin.repoository.PingSettingsRepository;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@ApplicationScoped
@Slf4j
public class PingService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final List<IpObj> ipObjList = Collections.synchronizedList(new ArrayList<>());
    private final Lock pauseLock = new ReentrantLock();
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
private final Semaphore semaphore = new Semaphore(2000); // Limit concurrent tasks


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


    }

    private void schedulePing() {
        scheduler.scheduleWithFixedDelay(this::pingI, 0, 60, TimeUnit.SECONDS);
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
                .onItem()
                .transformToUniAndMerge(pi ->
                        Uni.createFrom().completionStage(() -> {
                            if (semaphore.tryAcquire()) {
                                return CompletableFuture.supplyAsync(() -> {
                                    try {
                                        return pi.call();
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    } finally {
                                        semaphore.release();
                                    }
                                }, pingExecutor);
                            } else {
                                return CompletableFuture.failedFuture(new RejectedExecutionException("Task rejected due to semaphore limit"));
                            }
                        }))
                .collect().asList()
                .emitOn(pingExecutor)
                .onItem()
                .invoke(savePingResultList -> {
                    log.info("Saving Ping Result: {}", savePingResultList.size());
                    pingResultRepository.saveAll(savePingResultList);

                })
                .runSubscriptionOn(pingExecutor)
                .subscribe()
                .with(pingResults -> log.info("Ping results processed successfully"),
                        failure -> log.error("Failed to save Ping Result: {}", failure.getMessage(), failure.getCause())
                );


    }


    public int updateIpModel(String newIpAddress, String oldIpAddress, UUID id) {
        pauseLock.lock();
        try {
            var index = ipObjList.indexOf(new IpObj(oldIpAddress, id));
            if (index != -1) {
                ipObjList.set(index, new IpObj(newIpAddress, id));
                return 1;
            }
        } finally {
            pauseLock.unlock();
        }
        return -1;
    }


}

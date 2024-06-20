package com.georgebanin.serviceimpl;

import com.georgebanin.controller.ChatWebSocket;
import com.georgebanin.model.IpObj;
import com.georgebanin.model.PingResult;
import com.georgebanin.repoository.IPModelRepository;
import com.georgebanin.repoository.PingResultRepository;
import com.georgebanin.repoository.PingSettingsRepository;
import com.georgebanin.utils.LinuxApplePingCommands;
import com.georgebanin.utils.WindowsPingCommands;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.buffer.Buffer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
@Slf4j
public class PingService {

    @Inject
    IPModelRepository ipModelRepository;

    @Inject
    PingSettingsRepository pingSettingsRepository;

    @Inject
    PingResultRepository pingResultRepository;

    @Inject
    ChatWebSocket chatWebSocket;

    @Inject
    @Named("pingExecutor")
    ExecutorService pingExecutor;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private List<IpObj> ipObjList = Collections.synchronizedList(new ArrayList<>());
    private static Set<PingResult> pingResultSet = Collections.synchronizedSet(new HashSet<>());

    private final String OS = System.getProperty("os.name").toLowerCase();



    private String countSize = null;
    private String packetSize = null;


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
        scheduler.scheduleWithFixedDelay(this::pingI, 0, 90, TimeUnit.SECONDS);
    }
    public void pingI() {
        if (ipObjList.isEmpty()) {
            log.warn("IP list is empty, skipping ping.");
            return;
        }
        Multi.createFrom()
                .iterable(ipObjList)
                        .onItem()
                .invoke(ip -> pingExecutor.submit(() -> {
            try {
                pingIp(ip);
            } catch (IOException e) {
                log.error("Error pinging IP {}: {}", ip.getIpAddress(), e.getMessage());
            }
        })).subscribe().with(ipObj -> log.debug("pinging ip {}", ipObj.getIpAddress()));
        
    }

    private void pingIp(IpObj ip) throws IOException {
        ProcessBuilder processBuilder;

        if(OS.contains("windows")){
             processBuilder = new ProcessBuilder("ping", ip.getIpAddress(), WindowsPingCommands.count, countSize,WindowsPingCommands.packetSize,packetSize);
            var startTime = OffsetDateTime.now();
            Process process = processBuilder.start();
            var endTime = OffsetDateTime.now();
            buildWindowsPingResults(process,ip,startTime,endTime);
        } else if (OS.contains("linux") || OS.contains("mac") || OS.contains("ubuntu")) {
            processBuilder = new ProcessBuilder("ping", ip.getIpAddress(), LinuxApplePingCommands.count, countSize,LinuxApplePingCommands.packetSize,packetSize);
            var startTime = OffsetDateTime.now();
            Process process = processBuilder.start();
            var endTime = OffsetDateTime.now();
            buildLinuxApplePingResults(process,ip,startTime,endTime);
        }else {
            log.warn("Unsupported OS: {}", OS);
            throw new RuntimeException("Unsupported OS: " + OS);
        }

    }

    private void buildWindowsPingResults(Process process, IpObj ip, OffsetDateTime startTime, OffsetDateTime endTime) throws IOException {
        Buffer buffer = Buffer.buffer(process.getInputStream().readAllBytes());
        String pingOutput = buffer.toString();

        PingResult pingResult = new PingResult();
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("id",pingResult.getId());
        jsonObject.put("ipAddress",ip.getIpAddress());
        jsonObject.put("pingStartTime",startTime);
        Arrays.stream(pingOutput.split("\n")).forEach(line -> {
            if(line.contains(("Minimum = "))) {
                String[] parts = line.split(" = |ms, |ms, |ms");
                double min = parseToDouble(parts[1]);
                double max = parseToDouble(parts[3]);
                double avg = parseToDouble(parts[5]);
                jsonObject.put("rrtMin", min);
                jsonObject.put("rrtAvg", avg);
                jsonObject.put("rrtMax", max);
                pingResult.setLatency(avg);
            }
            if (line.contains("Packets:")) {
                String[] parts = line.split(",\\s+");
                String packetsSentStr = parts[0].split("=")[1].trim();
                double packetsSent = parseToDouble(packetsSentStr);
                String packetsReceivedStr = parts[1].split("=")[1];
                double packetsReceived = parseToDouble(packetsReceivedStr);
                String packetLossStr = parts[2].substring(parts[2].indexOf("(") + 1, parts[2].indexOf(")")).trim();
                double packetLossRate = parseToDouble(packetLossStr);
                jsonObject.put("packetsSent", packetsSent);
                jsonObject.put("packetsReceived", packetsReceived);
                pingResult.setPacketLossRate(packetLossRate);
            }
        });
        pingResult.setPingSuccess(true);
        pingResult.setPingStartTime(startTime);
        pingResult.setPingEndTime(endTime);
        pingResult.setAdditionalInfo(jsonObject);
        pingResult.setCreatedBy("Quartz");
        pingResult.setIpAddress(ip.getIpAddress());
        log.debug(pingResult.toString());
//        sendPingResult(pingResult);
        chatWebSocket.broadcast(jsonObject.toString());

        savePingResultList(pingResult);




    }

    private void buildLinuxApplePingResults(Process process, IpObj ip, OffsetDateTime startTime, OffsetDateTime endTime) throws IOException {
        //get the repository bean from the context
        Buffer buffer = Buffer.buffer(process.getInputStream().readAllBytes());
        String pingOutput = buffer.toString();

        PingResult pingResult = new PingResult();
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("id",pingResult.getId());
        jsonObject.put("ipAddress",ip.getIpAddress());
        jsonObject.put("pingStartTime",startTime);
        Arrays.stream(pingOutput.split("\n")).forEach(line -> {

            if(line.contains("data bytes")){
                String[] parts = line.split(":");
                String[] subParts = parts[1].split(" ");
                int packetSize = Integer.parseInt(subParts[1]);
                jsonObject.put("packetSize", packetSize);
            }
            if(line.contains(("min/avg/max"))){
                String[] parts = line.split(" ");
                String[] values = parts[3].split("/");
                double  min = parseToDouble(values[0]);
                double  avg = parseToDouble(values[1]);
                double  max = parseToDouble(values[2]);
                double  mdev = parseToDouble(values[3]);
                jsonObject.put("rrtMin", min);
                jsonObject.put("rrtAvg", avg);
                jsonObject.put("rrtMax", max);
                jsonObject.put("rrtMdev", mdev);
                pingResult.setLatency(avg);

            }
            if (line.contains("packet loss")) {
                String[] parts = line.split(", ");

                String packetsReceivedNumber = parts[1];
                String[] packetReceivedParts = packetsReceivedNumber.split(" ");
                double  packetsReceived = Integer.parseInt(packetReceivedParts[0]);

                String packetsSentNumber = parts[0];
                String[] packetSentParts = packetsSentNumber.split(" ");
                double  packetsSent = Integer.parseInt(packetSentParts[0]);

                String packetLossStr = parts[2];
                String[] packetLossParts = packetLossStr.split(" ");
                double  packetLossRate = parseToDouble(packetLossParts[0].replaceAll("%", ""));
                jsonObject.put("packetsSent",packetsSent);
                jsonObject.put("packetsReceived",packetsReceived);
                pingResult.setPacketLossRate(packetLossRate);

            }




        });
        pingResult.setPingSuccess(true);
        pingResult.setPingStartTime(startTime);
        pingResult.setPingEndTime(endTime);
        pingResult.setAdditionalInfo(jsonObject);
        pingResult.setCreatedBy("Quartz");
        pingResult.setIpAddress(ip.getIpAddress());




        log.debug(pingResult.toString());
        chatWebSocket.broadcast(jsonObject.toString());
        savePingResultList(pingResult);
    }

    private void savePingResultList(PingResult pingResult) {
        synchronized (pingResultSet) {
            pingResultSet.add(pingResult);
            log.debug("Added ping result, current set size: {}", pingResultSet.size());
            if (pingResultSet.size() == 3624) {
                try {
                    log.info("Saving {} ping results", pingResultSet.size());
                    pingResultRepository.saveAll(pingResultSet);
                    log.info("Successfully saved ping results");
                } catch (Exception e) {
                    log.error("Failed to save ping results: {}", e.getMessage());
                    return;
                }
                pingResultSet.clear();
                log.info("Cleared ping result set");
            }
        }
    }



    private double parseToDouble(String value){
        try {
            return Double.parseDouble(value);
        }catch (NumberFormatException e){
            return 0;
        }
    }


}

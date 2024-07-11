package com.georgebanin.serviceimpl;

import com.georgebanin.controller.ChatWebSocket;
import com.georgebanin.model.IpObj;
import com.georgebanin.model.PingResult;
import com.georgebanin.utils.LinuxApplePingCommands;
import com.georgebanin.utils.WindowsPingCommands;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.buffer.Buffer;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class PingTask  {

    private IpObj ipObj;

    private final String OS = System.getProperty("os.name").toLowerCase();

    public PingTask(IpObj ipObj) {
        this.ipObj = ipObj;
    }

    @Inject
    ChatWebSocket chatWebSocket;



    public PingResult call() throws Exception {
        log.debug("Starting ping {}",ipObj.getIpAddress() );
        return pingIp(ipObj);
    }

    private PingResult pingIp(IpObj ip) throws IOException, InterruptedException {
        ProcessBuilder processBuilder;

        if(OS.contains("windows")){
            processBuilder = new ProcessBuilder("ping", ip.getIpAddress(), WindowsPingCommands.count, "2");
            var startTime = OffsetDateTime.now();
            Process process = processBuilder.start();
            boolean finished = process.waitFor(500, TimeUnit.MILLISECONDS);
            if(!finished){
                process.destroy();
            }

            var endTime = OffsetDateTime.now();
           return buildWindowsPingResults(process,ip,startTime,endTime);



        } else if (OS.contains("linux") || OS.contains("mac") || OS.contains("ubuntu")) {
            processBuilder = new ProcessBuilder("ping", ip.getIpAddress(), LinuxApplePingCommands.count, "2");
            var startTime = OffsetDateTime.now();
            Process process = processBuilder.start();
//            boolean finished = process.waitFor(1000, TimeUnit.MILLISECONDS);
            var endTime = OffsetDateTime.now();
            return  buildLinuxApplePingResults(process,ip,startTime,endTime);

        }else {
            log.warn("Unsupported OS: {}", OS);
            return null;

//            throw new RuntimeException("Unsupported OS: " + OS);
        }

    }

    private PingResult buildLinuxApplePingResults(Process process, IpObj ip, OffsetDateTime startTime, OffsetDateTime endTime) throws IOException {
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

            }else {
                jsonObject.put("rrtMin", 0.0);
                jsonObject.put("rrtAvg", 0.0);
                jsonObject.put("rrtMax", 0.0);
                jsonObject.put("rrtMdev", 0.0);
                pingResult.setLatency(0.0);
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
                jsonObject.put("packetLossRate",packetLossRate);
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
//        chatWebSocket.broadcast(jsonObject.toString());
//        savePingResultList(pingResult);
        return pingResult;
    }

    private PingResult buildWindowsPingResults(Process process, IpObj ip, OffsetDateTime startTime, OffsetDateTime endTime) throws IOException {
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
            } else {
                jsonObject.put("rrtMin", 0.0);
                jsonObject.put("rrtAvg", 0.0);
                jsonObject.put("rrtMax", 0.0);
                jsonObject.put("rrtMdev", 0.0);
                pingResult.setLatency(0.0);
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
                jsonObject.put("packetLossRate",packetLossRate);
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
//        chatWebSocket.broadcast(jsonObject.toString());
//
//        savePingResultList(pingResult);
        return pingResult;

    }


    private double parseToDouble(String value){
        try {
            return Double.parseDouble(value);
        }catch (NumberFormatException e){
            return 0;
        }
    }

}

package com.georgebanin.model;


import io.vertx.core.json.JsonObject;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PingResult implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UUID id  = UUID.randomUUID();
    private String ipAddress;
    private OffsetDateTime pingStartTime;
    private OffsetDateTime pingEndTime;
    private Boolean pingSuccess;
    private Double latency;
    private Double packetLossRate;
    private JsonObject additionalInfo;
    private String updatedBy;
    private String createdBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

}

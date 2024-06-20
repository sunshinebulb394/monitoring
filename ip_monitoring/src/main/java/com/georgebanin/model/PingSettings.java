package com.georgebanin.model;

import lombok.*;

import java.time.OffsetDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PingSettings {

    private String id;
    private String name;
    private Integer count;
    private Integer packetSize;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;


}

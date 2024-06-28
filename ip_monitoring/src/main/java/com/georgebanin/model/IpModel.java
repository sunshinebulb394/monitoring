package com.georgebanin.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class IpModel {

    private UUID id ;
    private String ipAddress;
    private String ipGroup;
    private Boolean isEnabled;
    private String updatedBy;
    private String createdBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

}

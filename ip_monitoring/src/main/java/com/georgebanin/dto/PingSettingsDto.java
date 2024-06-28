package com.georgebanin.dto;

import lombok.*;

@Builder
public record PingSettingsDto(String id,String name,Integer packetSize,Integer count,String os) {
}

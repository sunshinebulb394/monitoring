package com.georgebanin.dto;


import lombok.Builder;

import java.time.OffsetDateTime;
@Builder
public record StatsDto(OffsetDateTime fromDate,OffsetDateTime toDate) {
}

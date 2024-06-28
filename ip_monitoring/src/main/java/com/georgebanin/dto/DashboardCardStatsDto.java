package com.georgebanin.dto;

import lombok.Builder;

@Builder
public record DashboardCardStatsDto(String cardName,Long total) {

}

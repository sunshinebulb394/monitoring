package com.georgebanin.service;

import com.georgebanin.dto.DashboardCardStatsDto;
import com.georgebanin.dto.ResponseDto;
import com.georgebanin.dto.StatsDto;
import io.smallrye.mutiny.Uni;

public interface StatisticsService {

    Uni<ResponseDto> dashboardCardStats();

    Uni<ResponseDto> calculateAvgLatencyByRange(StatsDto statsDto);
}

package com.georgebanin.controller;

import com.georgebanin.dto.IpModelDto;
import com.georgebanin.dto.ResponseDto;
import com.georgebanin.dto.StatsDto;
import com.georgebanin.exceptions.IpModelException;
import com.georgebanin.service.IpModelService;
import com.georgebanin.service.StatisticsService;
import io.quarkus.vertx.web.Body;
import io.quarkus.vertx.web.Param;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

import java.util.UUID;

@RouteBase(path = "/api/v1/stats")
public class StatisticsController {

    @Inject
    StatisticsService statisticsService;

    @Route(path = "/ip", methods = Route.HttpMethod.GET,produces = "application/json")
    public Uni<ResponseDto> getById() {
        return statisticsService.dashboardCardStats();
    }

    @Route(path = "/ip", methods = Route.HttpMethod.POST,produces = "application/json")
    public Uni<ResponseDto> calculateAvgLatencyByRange(@Body StatsDto statsDto) {
        return statisticsService.calculateAvgLatencyByRange(statsDto);
    }

}

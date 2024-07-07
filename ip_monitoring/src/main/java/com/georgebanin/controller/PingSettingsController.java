package com.georgebanin.controller;

import com.georgebanin.dto.PingSettingsDto;
import com.georgebanin.dto.ResponseDto;
import com.georgebanin.exceptions.ObjectNotValidException;
import com.georgebanin.service.PingSettingsService;
import com.georgebanin.serviceimpl.PingService;
import io.quarkus.vertx.web.Body;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;


@RequestScoped
@RouteBase(path = "/ping-settings")
public class PingSettingsController {

    @Inject
    PingSettingsService pingSettingsService;



    @Route(path = "/save", methods = Route.HttpMethod.POST,produces = "application/json")
    public Uni<ResponseDto> savePingSetting(@Body PingSettingsDto pingSettingsDto) throws ObjectNotValidException {
        return pingSettingsService.savePingSettings(pingSettingsDto);
    }

    @Route(path = "/all", methods = Route.HttpMethod.GET,produces = "application/json")
    public Uni<ResponseDto> getAllPingSettings() {
        return pingSettingsService.getAllPingSettings();
    }



}

package com.georgebanin.controller;

import com.georgebanin.dto.IpModelDto;
import com.georgebanin.dto.ResponseDto;
import com.georgebanin.exceptions.IpModelException;
import com.georgebanin.exceptions.ObjectNotValidException;
import com.georgebanin.model.IpModel;
import com.georgebanin.repoository.IPModelRepository;
import com.georgebanin.service.IpModelService;

import io.quarkus.vertx.web.Body;
import io.quarkus.vertx.web.Param;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.mutiny.core.buffer.Buffer;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import org.jboss.resteasy.reactive.RestForm;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

@RequestScoped
@RouteBase(path = "/ipmodel")
public class IPModelController {

    @Inject
    IpModelService ipModelService;

    @Inject
    IPModelRepository ipModelRepository;


    @Route(path = ":id", methods = Route.HttpMethod.GET,produces = "application/json")
    public Uni<ResponseDto> getById(@Param("id") String id) throws IpModelException {
       return ipModelService.getById(UUID.fromString(id));
    }

    @Route(path = "", methods = Route.HttpMethod.GET,produces = "application/json")
    public Multi<?> getAll(){
        return ipModelRepository.findAllEnabledIps();
    }

    @Route(path = "/upload/ip-file", methods = Route.HttpMethod.POST)
    public Uni<?> uploadIpFile(RoutingContext context) throws IOException {
       var uploads =  context.fileUploads();
      return   ipModelService.uploadIpFile(uploads);
    }

    @Route(path = "/add", methods = Route.HttpMethod.POST,produces = "application/json",consumes = "application/json")
    public Uni<?> addIp(@Body IpModelDto ipModelDto) throws ObjectNotValidException, IpModelException {
        return ipModelService.createIpModel(ipModelDto);
    }

    @Route(path = "/update", methods = Route.HttpMethod.PUT,produces = "application/json",consumes = "application/json")
    public Uni<?> updateIp(@Body JsonObject body) throws ObjectNotValidException, IpModelException {
        return ipModelService.updateIpModel(body);
    }

}

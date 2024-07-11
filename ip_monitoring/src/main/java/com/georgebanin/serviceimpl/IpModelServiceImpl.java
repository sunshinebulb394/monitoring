package com.georgebanin.serviceimpl;

import com.georgebanin.dto.IpModelDto;
import com.georgebanin.dto.ResponseDto;
import com.georgebanin.exceptions.IpModelException;
import com.georgebanin.exceptions.ObjectNotValidException;
import com.georgebanin.model.IpModel;
import com.georgebanin.repoository.IPModelRepository;
import com.georgebanin.service.IpModelService;
import com.georgebanin.validator.ObjectsValidator;

import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.parsetools.RecordParser;
import io.vertx.ext.web.FileUpload;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.UnknownHostException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.georgebanin.utils.PingUtilitiez.checkIfIpIsValidIp;

@ApplicationScoped
@Slf4j
public class IpModelServiceImpl implements IpModelService {

    @Inject
    IPModelRepository ipModelRepository;

    @Inject
    Vertx vertx;

//    @Inject
//    @Named("GeoIpReader")
//    DatabaseReader geoIpReader;

    @Inject
    ObjectsValidator<IpModelDto> objectsValidator;

    @Inject
    PingService pingService;

    @Override
    public Uni<ResponseDto> getById(UUID id) throws IpModelException {
        return   ipModelRepository.findById(id)
                .map(res -> ResponseDto.transformToResponse("Successfull",200,res));
    }

    @Override
    public Uni<ResponseDto> getAllIps() {
        return ipModelRepository.findAll().collect().asList().map(res -> ResponseDto.transformToResponse("Successfull",200,res));
    }

    @Override
    public Uni<ResponseDto> createIpModel(IpModelDto ipModelDto) throws IpModelException, ObjectNotValidException {
        objectsValidator.validate(ipModelDto);
        IpModel ipModel = new IpModel();
        ipModel.setIpAddress(ipModelDto.ipAddress());
        ipModel.setIpGroup(ipModel.getIpGroup());
        return ipModelRepository.save(ipModel).map(res -> ResponseDto.transformToResponse("Successfull",200,res));
    }

    @Override
    public Uni<?> uploadIpFile(List<FileUpload> uploads) throws IOException {
        var ipUpload =  uploads.get(0);

        AsyncFile asyncFile = vertx.fileSystem().openBlocking(ipUpload.uploadedFileName(), new OpenOptions());
        RecordParser recordParser = RecordParser.newDelimited("\n", bufferedLine -> {
            String[] stringSplit = bufferedLine.toString().split(",");
            try {
                if(checkIfIpIsValidIp(stringSplit[0].trim())){
                    IpModel ipModel = new IpModel();
                    ipModel.setIpAddress(stringSplit[0].trim());
                    ipModel.setIpGroup(stringSplit[1].trim());
                    ipModelRepository.save(ipModel).subscribe().with(System.out::println);

                }
            } catch (ObjectNotValidException | UnknownHostException e) {
                throw new RuntimeException(e);
            }
        });

        asyncFile.handler(recordParser)
                .endHandler(v -> {
                    asyncFile.close();
                    log.info("file upload complete");
                });

        return Uni.createFrom().item(new ResponseDto("Successfull",200,"Upload successfull", OffsetDateTime.now()));


    }

    @Override
    public Uni<?> updateIpModel(JsonObject body) throws IpModelException, ObjectNotValidException {
        var oldId = UUID.fromString(body.getString("id"));
        var oldIpAddress = body.getString("oldIpAddress");
        var newIp = body.getString("newIpAddress");
        var newIpGroup = body.getString("newIpGroup");

        var dto = IpModelDto.builder().ipAddress(newIp).ipGroup(newIpGroup).build();
        objectsValidator.validate(dto);
     return  ipModelRepository
                .findById(oldId)
                        .onItem()
                .transform(ipModel -> {
                    ipModel.setIpAddress(newIp);
                    ipModel.setIpGroup(newIpGroup);
                    return ipModel;
                })
                 .flatMap(ipModel -> {
                     try {
                         return ipModelRepository.update(ipModel);
                     } catch (ObjectNotValidException e) {
                         // Handle the exception and return a failed Uni
                         return Uni.createFrom().failure(new ObjectNotValidException(e.getMessage()));
                     }
                 })
                 .call(ipModel -> Uni.createFrom().item(pingService.updateIpModel(newIp, oldIpAddress,ipModel.getId())))
                 .map(ipModel -> ResponseDto.transformToResponse("Successfull",200,ipModel));
    }


}

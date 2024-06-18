package com.georgebanin.service;

import com.georgebanin.dto.IpModelDto;
import com.georgebanin.dto.ResponseDto;
import com.georgebanin.exceptions.IpModelException;
import com.georgebanin.exceptions.ObjectNotValidException;
import com.georgebanin.model.IpModel;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.FileUpload;
import io.vertx.mutiny.core.buffer.Buffer;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public interface IpModelService {
    Uni<ResponseDto> getById(UUID id) throws IpModelException;
    Uni<ResponseDto> getAllIps();
    Uni<ResponseDto> createIpModel(IpModelDto ipModelDto) throws IpModelException, ObjectNotValidException;

    Uni<?> uploadIpFile(List<FileUpload> uploads) throws IOException;
}

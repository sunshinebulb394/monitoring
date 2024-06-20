package com.georgebanin.service;

import com.georgebanin.dto.PingSettingsDto;
import com.georgebanin.dto.ResponseDto;
import com.georgebanin.exceptions.ObjectNotValidException;
import io.smallrye.mutiny.Uni;

public interface PingSettingsService {

    Uni<ResponseDto> savePingSettings(PingSettingsDto pingSettingsDto) throws ObjectNotValidException;

    Uni<ResponseDto> getAllPingSettings();

}

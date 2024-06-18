package com.georgebanin.serviceimpl;

import com.georgebanin.dto.PingSettingsDto;
import com.georgebanin.dto.ResponseDto;
import com.georgebanin.exceptions.ObjectNotValidException;
import com.georgebanin.model.PingSettings;
import com.georgebanin.repoository.PingSettingsRepository;
import com.georgebanin.service.PingSettingsService;
import com.georgebanin.validator.ObjectsValidator;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class PingSettingsServiceImpl implements PingSettingsService {

    @Inject
    PingSettingsRepository pingSettingsRepository;

    @Inject
    ObjectsValidator<PingSettingsDto> pingSettingsValidator;

    @Override
    public Uni<ResponseDto> savePingSettings(PingSettingsDto pingSettingsDto) throws ObjectNotValidException {
        pingSettingsValidator.validate(pingSettingsDto);
        PingSettings pingSettings = new PingSettings();
        pingSettings.setPacketSize(pingSettingsDto.packetSize());
        pingSettings.setCount(pingSettingsDto.count());
        pingSettings.setName(pingSettingsDto.name());
        pingSettings.setCreatedBy("ADMIN");

       return pingSettingsRepository.save(pingSettings)
                .map(setting -> ResponseDto.transformToResponse("successfull",200,setting));

    }

    @Override
    public Uni<ResponseDto> getAllPingSettings() {
        return pingSettingsRepository.findAll()
                .collect()
                .asList()
                .map(settings -> ResponseDto.transformToResponse("successfull",200,settings));
    }
}

package com.georgebanin.validator;

import com.georgebanin.dto.PingSettingsDto;
import com.georgebanin.enums.OSType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class PingSettingValidator implements ConstraintValidator<CustomValidation, PingSettingsDto> {
    @Override
    public void initialize(CustomValidation constraintAnnotation) {
    }

    @Override
    public boolean isValid(PingSettingsDto value, ConstraintValidatorContext context) {
        if(value.count() > 10){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("The number of echo request should not exceed 10").addConstraintViolation();
            return false;
        }
        if(value.packetSize() > 150000){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("The packet size should not exceed 1MB").addConstraintViolation();
            return false;
        }
        if(Arrays.stream(OSType.values()).noneMatch(os -> os.name().equalsIgnoreCase(value.os()))){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid OS Type").addConstraintViolation();
            return false;
        }
        return true;
    }
}

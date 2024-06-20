package com.georgebanin.dto;

import com.georgebanin.validator.CustomValidation;
import lombok.*;

@Builder
@CustomValidation
public record IpModelDto(String ipAddress,String ipGroup) { }

package com.georgebanin.dto;

import lombok.*;

import java.time.OffsetDateTime;

@Builder
public record ResponseDto (String message,Integer status,Object data,OffsetDateTime timestamp){


    public static ResponseDto transformToResponse(String message,Integer status, Object data) {
        return ResponseDto.builder().message(message)
                .status(status)
                .data(data)
                .timestamp(OffsetDateTime.now())
                .build();
    }

}

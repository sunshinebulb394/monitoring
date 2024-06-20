package com.georgebanin.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;

public class IpModelException extends Exception{

    String message;
    HttpResponseStatus status;

    public IpModelException(String message, HttpResponseStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }


}

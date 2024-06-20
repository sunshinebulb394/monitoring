package com.georgebanin.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;

public class ObjectNotValidException extends Exception{
    String message;
    HttpResponseStatus status;

    public ObjectNotValidException(String message) {
        super(message);
        this.message = message;
        this.status = HttpResponseStatus.BAD_REQUEST;
    }
}

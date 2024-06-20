package com.georgebanin.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;

public class PingSettingsException extends Exception {
    private  String message;
    private HttpResponseStatus httpResponseStatus;
    public PingSettingsException(String message) {
        super(message);
        this.message = message;
    }

    public PingSettingsException(String message,HttpResponseStatus httpResponseStatus) {
        super(message);
        this.message = message;
    }

}

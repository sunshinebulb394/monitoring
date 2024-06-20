package com.georgebanin.exceptions;

public class PingResultException extends Exception{

    private String message;

    public PingResultException(String message) {
        super(message);
        this.message = message;
    }

}

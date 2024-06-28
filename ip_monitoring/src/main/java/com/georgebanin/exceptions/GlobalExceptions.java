package com.georgebanin.exceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptions implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {
        return Response.
                status(400)
                .entity(exception.getMessage())
                .tag("SuccessFul")
                .build();
    }
}

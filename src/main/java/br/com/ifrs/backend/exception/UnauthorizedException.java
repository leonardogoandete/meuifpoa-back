package br.com.ifrs.backend.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.Collections;

public class UnauthorizedException extends WebApplicationException {

    public UnauthorizedException(String message) {
        super(Response.status(Response.Status.UNAUTHORIZED)
                .entity(Collections.singletonMap("erro", message))
                .build());
    }
}

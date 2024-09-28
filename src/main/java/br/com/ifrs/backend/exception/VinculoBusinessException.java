package br.com.ifrs.backend.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.Collections;

public class VinculoBusinessException extends WebApplicationException {
    public VinculoBusinessException(String message) {
        super(Response.status(Response.Status.NOT_ACCEPTABLE)
                .entity(Collections.singletonMap("erro", message))
                .build());
    }
}

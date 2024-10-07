package br.com.ifrs.backend.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.Collections;

/**
 * The type Unauthorized exception.
 */
public class UnauthorizedException extends WebApplicationException {

    /**
     * Instantiates a new Unauthorized exception.
     *
     * @param message the message
     */
    public UnauthorizedException(String message) {
        super(Response.status(Response.Status.UNAUTHORIZED)
                .entity(Collections.singletonMap("erro", message))
                .build());
    }
}

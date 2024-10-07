package br.com.ifrs.backend.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.Collections;

/**
 * The type Vinculo business exception.
 */
public class VinculoBusinessException extends WebApplicationException {
    /**
     * Instantiates a new Vinculo business exception.
     *
     * @param message the message
     */
    public VinculoBusinessException(String message) {
        super(Response.status(Response.Status.NOT_ACCEPTABLE)
                .entity(Collections.singletonMap("erro", message))
                .build());
    }
}

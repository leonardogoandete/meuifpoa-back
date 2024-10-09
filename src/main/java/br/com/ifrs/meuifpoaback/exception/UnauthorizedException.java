package br.com.ifrs.meuifpoaback.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.Collections;

/**
 * A classe UnauthorizedException representa uma exceção para situações de não autorização.
 */
public class UnauthorizedException extends WebApplicationException {

    /**
     * Instancia uma nova UnauthorizedException.
     *
     * @param message a mensagem de erro
     */
    public UnauthorizedException(String message) {
        super(Response.status(Response.Status.UNAUTHORIZED)
                .entity(Collections.singletonMap("erro", message))
                .build());
    }
}

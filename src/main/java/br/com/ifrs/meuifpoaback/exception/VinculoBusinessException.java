package br.com.ifrs.meuifpoaback.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.Collections;

/**
 * A classe VinculoBusinessException representa uma exceção para situações de vínculo não aceitável.
 */
public class VinculoBusinessException extends WebApplicationException {
    /**
     * Instancia uma nova VinculoBusinessException.
     *
     * @param message a mensagem de erro
     */
    public VinculoBusinessException(String message) {
        super(Response.status(Response.Status.NOT_ACCEPTABLE)
                .entity(Collections.singletonMap("erro", message))
                .build());
    }
}

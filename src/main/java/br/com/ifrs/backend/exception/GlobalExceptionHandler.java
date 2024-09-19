package br.com.ifrs.backend.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.Collections;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);

    @Override
    public Response toResponse(Throwable exception) {
        // Log da exceção para facilitar o rastreamento
        LOG.error("Unhandled exception occurred: ", exception);

        // Verifica se a exceção é do tipo UnauthorizedException
        if (exception instanceof UnauthorizedException) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Collections.singletonMap("mensagem", exception.getMessage()))
                    .build();
        }

        // Trata exceções genéricas e inesperadas
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Collections.singletonMap("erro", "Erro interno do servidor."))
                .build();
    }
}

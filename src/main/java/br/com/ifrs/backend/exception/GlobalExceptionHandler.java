package br.com.ifrs.backend.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.Collections;

/**
 * The type Global exception handler.
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);

    @Override
    public Response toResponse(Throwable exception) {
        LOG.error("Unhandled exception occurred: ", exception);

        if (exception instanceof UnauthorizedException) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Collections.singletonMap("mensagem", exception.getMessage()))
                    .build();
        }

        if (exception instanceof IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Collections.singletonMap("mensagem", exception.getMessage()))
                    .build();
        }

        if (exception instanceof VinculoBusinessException) {
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity(Collections.singletonMap("mensagem", exception.getMessage()))
                    .build();
        }

        // Exceções não tratadas
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Collections.singletonMap("erro", "Erro interno do servidor. Consulte o log para mais detalhes."))
                .build();
    }
}


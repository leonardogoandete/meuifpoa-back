package br.com.ifrs.meuifpoaback.exception;

import br.com.ifrs.meuifpoaback.utils.FirestoreUtils;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Collections;
import java.util.logging.Logger;

/**
 * A classe GlobalExceptionHandler é responsável por tratar exceções não tratadas em todo o aplicativo.
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Throwable> {

    private static final java.util.logging.Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        logger.severe("Ocorreu uma exceção não tratada: " + exception.getMessage());

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


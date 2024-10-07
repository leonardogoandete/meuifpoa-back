package br.com.ifrs.backend.controller;

import br.com.ifrs.backend.exception.UnauthorizedException;
import br.com.ifrs.backend.model.Login;
import br.com.ifrs.backend.service.SyncService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A classe SyncController é responsável por gerenciar as requisições relacionadas à sincronização de dados do usuário.
 */
@Path("/sync")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SyncController {

    private static final Logger logger = Logger.getLogger(SyncController.class.getName());

    /**
     * O serviço de sincronização.
     */
    @Inject
    SyncService syncService;

    /**
     * Sincroniza os dados do usuário e salva no Firebase.
     *
     * @param securityContext o contexto de segurança
     * @param login           os dados de login do usuário
     * @return a resposta da sincronização
     */
    @POST
    @Operation(summary = "Sincronizar dados do usuário e salvar no Firebase")
    public Response sincronizar(@Context SecurityContext securityContext, Login login) {
        String userId = securityContext.getUserPrincipal().getName();
        Map<String, String> response = new HashMap<>();
        try {
            // Valida o login
            if (login.getSenha() == null || login.getSenha().isEmpty()) {
                response.put("mensagem", "A senha não pode ser vazia.");
                return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
            }

            // Executa a sincronização
            if (syncService.sincronizar(userId, login.getSenha())) {
                response.put("mensagem", "Sincronização realizada com sucesso!");
                return Response.ok().entity(response).build();
            } else {
                response.put("mensagem", "Erro ao sincronizar dados.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
            }
        } catch (UnauthorizedException e) {
            logger.log(Level.WARNING, "Falha de autorização durante a sincronização", e);
            response.put("mensagem", e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
        } catch (SecurityException e) {
            // Tratar exceção de segurança especificamente
            logger.log(Level.WARNING, "Falha de autorização (SecurityException)", e);
            response.put("mensagem", "Usuário não autorizado");
            return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro interno do servidor", e);
            response.put("mensagem", "Erro interno do servidor");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
        }
    }
}

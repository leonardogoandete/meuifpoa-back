package br.com.ifrs.meuifpoaback.controller;

import br.com.ifrs.meuifpoaback.exception.UnauthorizedException;
import br.com.ifrs.meuifpoaback.model.Login;
import br.com.ifrs.meuifpoaback.model.Perfil;
import br.com.ifrs.meuifpoaback.service.SyncService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import javax.annotation.security.RolesAllowed;
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
    @Inject
    JsonWebToken jwt;
    /**
     * O serviço de sincronização.
     */
    private final SyncService syncService;

    /**
     * Construtor da classe.
     *
     * @param syncService o serviço de sincronização
     */
    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    /**
     * Sincroniza os dados do usuário e salva no Firebase.
     *
     * @param securityContext o contexto de segurança
     * @param login           os dados de login do usuário
     * @return a resposta da sincronização
     */
    @POST
    @Operation(summary = "Sincronizar dados do usuário e salvar no Firebase")
    @APIResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Perfil.class)
            )
    )
    @RolesAllowed("*")
    public Response sincronizar(
            @Context SecurityContext securityContext,
            @RequestBody(required = true,
                    content = @Content(
                            mediaType = "application/json",
                            example = "{\"senha\":\"123456\"}",
                            schema = @Schema(implementation = Login.class)
                    )
            ) Login login) {
        String userId = jwt.getClaim("user_id");
        String email = jwt.getClaim("email");

        Map<String, String> response = new HashMap<>();
        try {
            // Valida o login
            if (login.getSenha() == null || login.getSenha().isEmpty()) {
                response.put("mensagem", "A senha não pode ser vazia.");
                return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
            }

            Perfil perfil = syncService.sincronizar(userId, email, login.getSenha());
            // Executa a sincronização
            if (perfil != null) {
                response.put("mensagem", "Sincronização realizada com sucesso!");
                return Response.ok().entity(perfil).build();
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

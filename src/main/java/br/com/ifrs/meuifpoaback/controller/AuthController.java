package br.com.ifrs.meuifpoaback.controller;


import br.com.ifrs.meuifpoaback.model.FirebaseAuthRequest;
import br.com.ifrs.meuifpoaback.model.FirebaseAuthResponse;
import br.com.ifrs.meuifpoaback.service.FirebaseAuthService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.Map;
import java.util.logging.Logger;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
/**
 * Classe que implementa o controlador REST para autenticação de usuários.
 */
public class AuthController {
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());
    /**
     * O Serviço de autenticação do Firebase.
     */
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Construtor que injeta o serviço de autenticação do Firebase.
     *
     * @param firebaseAuthService serviço de autenticação do Firebase
     */
    public AuthController(FirebaseAuthService firebaseAuthService) {
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * Método que realiza o login de um usuário.
     *
     * @param loginRequest objeto com os dados de login
     * @return resposta com o token de autenticação
     */
    @POST
    @Operation(summary = "Realiza o login de um usuário.")
    @APIResponse(responseCode = "200", description = "Login bem-sucedido.")
    public Response login(
            @RequestBody(required = true,
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        example = "{\"email\":\"email@email.com\",\"password\":\"password\",\"returnSecureToken\":true}",
                        schema = @Schema(implementation = FirebaseAuthRequest.class)
                )
            )
            FirebaseAuthRequest loginRequest) {
        try {
            FirebaseAuthResponse token = firebaseAuthService.login(loginRequest.email(), loginRequest.password(), loginRequest.returnSecureToken());
            return Response.ok(token).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(Map.of("erro",e.getMessage())).build();
        }
    }
}

package br.com.ifrs.backend.controller;

import br.com.ifrs.backend.exception.UnauthorizedException;
import br.com.ifrs.backend.model.Login;
import br.com.ifrs.backend.service.SyncService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/sync")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SyncController {
    private static final Logger logger = Logger.getLogger(SyncController.class.getName());
    private final SyncService syncService = new SyncService();

    @POST
    @Authenticated
    public Response sincronizar(@Context SecurityContext securityContext, Login login) {
        String userId = securityContext.getUserPrincipal().getName();
        Map<String, String> response = new HashMap<>();
        try {
            syncService.sincronizar(userId, login.getSenha());
            response.put("mensagem", "Sincronização realizada com sucesso!");
            return Response.ok().entity(response).build();
        }catch (Exception e) {
            logger.log(Level.SEVERE, "Erro interno do servidor", e);
            response.put("mensagem", "Erro interno do servidor");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(response)
                    .build();
        }
    }
}

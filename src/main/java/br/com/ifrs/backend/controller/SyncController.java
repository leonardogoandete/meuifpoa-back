package br.com.ifrs.backend.controller;

import br.com.ifrs.backend.exception.UnauthorizedException;
import br.com.ifrs.backend.model.Login;
import br.com.ifrs.backend.service.SyncService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/sync")
public class SyncController {
    private static final Logger logger = Logger.getLogger(SyncController.class.getName());
    private final SyncService syncService = new SyncService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sincronizar(@HeaderParam("Authorization") String token, Login login) {
        if (token == null || token.isEmpty()) {
            logger.log(Level.WARNING, "Token de autenticação não fornecido!");
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token de autenticação não fornecido!").build();
        }
        Map<String, String> response = new java.util.HashMap<>(Map.of());
        try {
            String uid = extractUidFromToken(token);
            syncService.sincronizar(uid, login.getSenha());
            response.put("mensagem", "Sincronização realizada com sucesso!");
            return Response.ok().entity(response).build();
        } catch (FirebaseAuthException e) {
            logger.log(Level.WARNING, "Token de autenticação inválido!");
            response.put("mensagem", "Token de autenticação inválido!");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(response)
                    .build();
        } catch (UnauthorizedException e) {
            response.put("mensagem", e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(response)
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro interno do servidor", e);
            response.put("mensagem", "Erro interno do servidor");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(response)
                    .build();
        }
    }

    private String extractUidFromToken(String token) throws FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        String uid = decodedToken.getUid();

        return uid;
    }
}

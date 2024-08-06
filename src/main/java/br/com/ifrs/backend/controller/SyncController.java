package br.com.ifrs.backend.controller;

import br.com.ifrs.backend.model.Login;
import br.com.ifrs.backend.service.SyncService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/sync")
@Consumes(MediaType.APPLICATION_JSON)
public class SyncController {
    private static final Logger logger = Logger.getLogger(SyncController.class.getName());
    private final SyncService syncService;
    public SyncController(SyncService syncService){
        this.syncService = syncService;
    }

    @POST()
    public Response sincronizarDados(@HeaderParam("Authorization") String token, Login login) {
        if (token == null || token.isEmpty()) {
            logger.log(Level.WARNING, "Token de autenticação não fornecido!");
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token de autenticação não fornecido!").build();
        }
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String uid = decodedToken.getUid();

            syncService.sincronizar(uid, login);

            logger.log(Level.INFO, "Iniciando Sincronização com o SIGAA");
            return Response.status(Response.Status.OK).build();
        }catch (FirebaseAuthException e) {
            logger.log(Level.WARNING, "Erro ao decodificar o token de autenticação: " + e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token expirado!").build();
        }catch (Exception e){
            logger.log(Level.WARNING, "Erro ao sincronizar com o SIGAA: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao sincronizar com o SIGAA!").build();
        }

    }


}

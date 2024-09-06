package br.com.ifrs.backend.controller;

import br.com.ifrs.backend.model.Login;
import br.com.ifrs.backend.service.SyncService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/sync")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SyncController {
    private static final Logger logger = Logger.getLogger(SyncController.class.getName());
    private final SyncService syncService;
    private static final String MSG_ERRO_SEM_TOKEN = "Token de autenticação não fornecido!";
    private static final String MSG_ERRO_DECODED_TOKEN = "Erro ao decodificar o token de autenticação: ";
    public SyncController(SyncService syncService){
        this.syncService = syncService;
    }

    @POST()
    public Response sincronizarDados(@HeaderParam("Authorization") String token, Login login) {
        Map<String, String> mensagem = new HashMap<>();

        if (token == null || token.isEmpty()) {
            logger.log(Level.WARNING, MSG_ERRO_SEM_TOKEN);
            mensagem.put("erro",MSG_ERRO_SEM_TOKEN);
            return Response.status(Response.Status.UNAUTHORIZED).entity(mensagem).build();
        }
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String uid = decodedToken.getUid();

            syncService.sincronizar(uid, login);

            logger.log(Level.INFO, "Iniciando Sincronização com o SIGAA");
            mensagem.put("status","Iniciando Sincronização com o SIGAA");
            return Response.status(Response.Status.OK).entity("").build();
        }catch (FirebaseAuthException e) {
            logger.log(Level.WARNING, MSG_ERRO_DECODED_TOKEN + e.getMessage());
            mensagem.put("erro",e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(mensagem).build();
        }catch (Exception e){
            logger.log(Level.WARNING, "Erro ao sincronizar com o SIGAA: " + e.getMessage());
            mensagem.put("erro",e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(mensagem).build();
        }

    }
}

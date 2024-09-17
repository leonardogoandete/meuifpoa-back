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

    // Mensagens de erro
    private static final String MSG_ERRO_SEM_TOKEN = "Token de autenticação não fornecido!";
    private static final String MSG_ERRO_DECODED_TOKEN = "Erro ao decodificar o token de autenticação: ";
    private static final String MSG_SINCRONIZACAO_INICIADA = "Iniciando Sincronização com o SIGAA";
    private static final String MSG_ERRO_SINCRONIZACAO = "Erro ao sincronizar com o SIGAA: ";

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    @POST
    public Response sincronizarDados(@HeaderParam("Authorization") String token, Login login) {
        Map<String, String> mensagem = new HashMap<>();

        if (token == null || token.isEmpty()) {
            logger.log(Level.WARNING, MSG_ERRO_SEM_TOKEN);
            mensagem.put("erro", MSG_ERRO_SEM_TOKEN);
            return Response.status(Response.Status.UNAUTHORIZED).entity(mensagem).build();
        }

        try {
            // Verifica o token do Firebase
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String uid = decodedToken.getUid();

            // Inicia o processo de sincronização
            syncService.sincronizar(uid, login);

            logger.log(Level.INFO, MSG_SINCRONIZACAO_INICIADA);
            mensagem.put("status", MSG_SINCRONIZACAO_INICIADA);
            return Response.status(Response.Status.OK).entity(mensagem).build();

        } catch (FirebaseAuthException e) {
            logger.log(Level.WARNING, MSG_ERRO_DECODED_TOKEN + e.getMessage());
            mensagem.put("erro", MSG_ERRO_DECODED_TOKEN + e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(mensagem).build();

        } catch (Exception e) {
            logger.log(Level.SEVERE, MSG_ERRO_SINCRONIZACAO + e.getMessage(), e);
            mensagem.put("erro", MSG_ERRO_SINCRONIZACAO + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(mensagem).build();
        }
    }
}

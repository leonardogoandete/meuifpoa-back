package br.com.ifrs.backend.service;

import br.com.ifrs.backend.model.Login;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class SyncService {

    private static final Logger logger = Logger.getLogger(NotasService.class.getName());
    private final Firestore db = FirestoreClient.getFirestore();

    public SyncService() {
    }

    public void sincronizar(String uid, Login login) {
        String cpf = getCpfFromFirestore(uid);
        if (cpf == null) {
            logger.warning("CPF não encontrado para o UID: " + uid);
            return;
        }

        logger.info("Iniciando sincronização com o SIGAA para o CPF: " + cpf);

        CompletableFuture<Void> notasFuture = CompletableFuture.runAsync(() -> {
            try {
                NotasService notasService = new NotasService();
                notasService.obterNotas(uid, login.getSenha());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Erro ao obter notas", e);
            }
        });

        CompletableFuture<Void> perfilFuture = CompletableFuture.runAsync(() -> {
            try {
                PerfilService perfilService = new PerfilService();
                perfilService.obterDadosUsuario(uid, login.getSenha());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Erro ao obter perfil", e);
            }
        });

        CompletableFuture<Void> allOf = CompletableFuture.allOf(notasFuture, perfilFuture);
        allOf.join(); // Isso bloqueia até que ambas as tarefas sejam concluídas
    }

    private String getCpfFromFirestore(String uid) {
        try {
            ApiFuture<DocumentSnapshot> future = db.collection("usuarios").document(uid).get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                return document.getString("cpf");
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Erro ao obter CPF do Firestore", e);
        }
        return null;
    }
}

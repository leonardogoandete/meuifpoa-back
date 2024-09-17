package br.com.ifrs.backend.service;

import br.com.ifrs.backend.model.Login;
import br.com.ifrs.backend.utils.FirestoreUtils;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class SyncService {

    private static final Logger logger = Logger.getLogger(SyncService.class.getName());
    private final FirestoreUtils firestoreUtils = new FirestoreUtils();

    public SyncService() {
    }

    public void sincronizar(String uid, Login login) {
        String cpf = firestoreUtils.getCpfFromFirestore(uid);
        if (cpf == null) {
            logger.warning("CPF não encontrado para o UID: " + uid);
            return;
        }

        logger.info("Iniciando sincronização com o SIGAA para o CPF: " + cpf);

        try {
            NotasService notasService = new NotasService();
            PerfilService perfilService = new PerfilService();

            // Execução em paralelo para otimizar o tempo
            CompletableFuture<Void> notasFuture = CompletableFuture.runAsync(() -> notasService.obterNotas(uid, login.getSenha()));
            CompletableFuture<Void> perfilFuture = CompletableFuture.runAsync(() -> perfilService.obterDadosUsuario(uid, login.getSenha()));
            CompletableFuture.allOf(notasFuture, perfilFuture).join();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao sincronizar com o SIGAA", e);
        }
    }
}

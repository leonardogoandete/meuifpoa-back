package br.com.ifrs.meuifpoaback.utils;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe utilitária para operações no Firestore.
 */
public class FirestoreUtils {

    private static final Logger logger = Logger.getLogger(FirestoreUtils.class.getName());

    /**
     * Obtém o CPF do Firestore.
     *
     * @param uid Identificador do usuário.
     * @return CPF do usuário ou null se não encontrado.
     */
    public String getCpfFromFirestore(String uid) {
        try {
            ApiFuture<DocumentSnapshot> future = FirestoreClient.getFirestore()
                    .collection("usuarios")
                    .document(uid)
                    .get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                return document.getString("cpf");
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Erro ao obter CPF do Firestore", e);
            Thread.currentThread().interrupt();
        }
        return null;
    }

    /**
     * Obtém o email do Firestore.
     *
     * @param uid Identificador do usuário.
     * @return Email do usuário ou null se não encontrado.
     */
    public String getEmailFromFirestore(String uid) {
        try {
            ApiFuture<DocumentSnapshot> future = FirestoreClient.getFirestore()
                    .collection("usuarios")
                    .document(uid)
                    .get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                return document.getString("email");
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Erro ao obter email do Firestore", e);
            Thread.currentThread().interrupt();
        }
        return null;
    }
}

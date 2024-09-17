package br.com.ifrs.backend.utils;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FirestoreUtils {

    private static final Logger logger = Logger.getLogger(FirestoreUtils.class.getName());

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

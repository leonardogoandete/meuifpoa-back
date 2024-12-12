package br.com.ifrs.meuifpoaback.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Classe responsável por inicializar o Firebase.
 */
@ApplicationScoped
public class FirebaseInitialize {

    private static final Logger logger = Logger.getLogger(FirebaseInitialize.class.getName());

    /**
     * Método chamado no início da aplicação para configurar o Firebase.
     *
     * @param ev evento de inicialização
     */
    void onStart(@Observes StartupEvent ev) {
        try {
            // Carrega as credenciais do Firebase a partir da variável de ambiente
            String firebaseCredentials = System.getenv("FIREBASE_CREDENTIALS");
            if (firebaseCredentials == null || firebaseCredentials.isEmpty()) {
                throw new IllegalStateException("A variável de ambiente FIREBASE_CREDENTIALS não está definida.");
            }

            InputStream serviceAccount = new ByteArrayInputStream(firebaseCredentials.getBytes());

            // Configura as opções do Firebase
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket("ifrspoa-d9f18.appspot.com")
                    .build();

            // Inicializa o Firebase com as opções configuradas
            FirebaseApp.initializeApp(options);

            logger.info("Firebase inicializado com sucesso.");
        } catch (IOException e) {
            logger.severe("Erro ao inicializar o Firebase: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalStateException e) {
            logger.severe("Erro na configuração do Firebase: " + e.getMessage());
        }
    }
}

package br.com.ifrs.meuifpoaback.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import java.io.IOException;
import java.io.InputStream;

/**
 * Classe responsável por inicializar o Firebase.
 */
@ApplicationScoped
public class FirebaseInitialize {

    /**
     * Método chamado no início da aplicação para configurar o Firebase.
     *
     * @param ev evento de inicialização
     */
    void onStart(@Observes StartupEvent ev) {
        try{
            // Carrega o arquivo de credenciais do Firebase
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");

            // Configura as opções do Firebase
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket("ifrspoa-d9f18.appspot.com")
                    .build();

            // Inicializa o Firebase com as opções configuradas
            FirebaseApp.initializeApp(options);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
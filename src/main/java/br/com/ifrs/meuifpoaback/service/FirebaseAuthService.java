package br.com.ifrs.meuifpoaback.service;

import br.com.ifrs.meuifpoaback.model.FirebaseAuthRequest;
import br.com.ifrs.meuifpoaback.model.FirebaseAuthResponse;
import com.google.gson.Gson;
import okhttp3.*;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Classe que implementa o serviço de autenticação do Firebase.
 */
@ApplicationScoped
public class FirebaseAuthService {

    private static final Logger logger = Logger.getLogger(FirebaseAuthService.class.getName());

    @ConfigProperty(name = "firebase.api.key")
    String FIREBASE_AUTH_URL;

    /**
     * Cliente HTTP para realizar requisições.
     */
    private final OkHttpClient client = new OkHttpClient();
    /**
     * Objeto Gson para serialização e desserialização de objetos JSON.
     */
    private final Gson gson = new Gson();


    /**
     * Método que realiza o login de um usuário no Firebase.
     *
     * @param email email do usuário
     * @param password senha do usuário
     * @param returnSecureToken indica se o token de autenticação deve ser retornado
     * @return FirebaseAuthResponse de autenticação
     */
    public FirebaseAuthResponse login(String email, String password, boolean returnSecureToken) throws IOException {
        FirebaseAuthRequest request = new FirebaseAuthRequest(email, password, returnSecureToken);
        String jsonBody = gson.toJson(request);
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));

        Request httpRequest = new Request.Builder()
                .url(FIREBASE_AUTH_URL)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try {
            Response response = client.newCall(httpRequest).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Erro na autenticação"+ response);
            }

            // Converte a resposta JSON para o objeto FirebaseAuthResponse
            FirebaseAuthResponse firebaseResponse = gson.fromJson(response.body().string(), FirebaseAuthResponse.class);

            logger.info("Login bem-sucedido para: " + email);
            return new FirebaseAuthResponse(
                    firebaseResponse.idToken()
            );

            //return firebaseResponse.idToken();

        } catch (IOException e) {
            logger.severe("Erro ao autenticar no Firebase: " + e.getMessage());
            throw e;
        }
    }
}

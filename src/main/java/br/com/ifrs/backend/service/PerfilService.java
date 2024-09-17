package br.com.ifrs.backend.service;

import br.com.ifrs.backend.exception.UnauthorizedException;
import br.com.ifrs.backend.model.Perfil;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class PerfilService {
    private static final Logger logger = Logger.getLogger(PerfilService.class.getName());
    private final Firestore db = FirestoreClient.getFirestore();
    private final StorageClient storage = StorageClient.getInstance();

    public Perfil obterDadosUsuario(String uid, String senha) {
        Perfil dadosUsuario = null;
        String cpf = getCpfFromFirestore(uid);
        String email = null;

        if (cpf == null) {
            logger.warning("CPF não encontrado para o UID: " + uid);
            return null;
        }

        // Consultar os dados do usuário no Firestore
        ApiFuture<DocumentSnapshot> future = db.collection("usuarios").document(uid).get();
        logger.info(">>>> Consultando dados do usuário no Firestore para o UID: " + uid);
        DocumentSnapshot document;
        try {
            document = future.get(); // Espera até que o resultado seja retornado
            if (document.exists()) {
                // Obtém os dados do documento
                cpf = document.getString("cpf");
                email = document.getString("email");
                logger.info("Dados do usuário recuperados do Firestore. CPF: " + cpf);
            } else {
                logger.warning("Documento não encontrado para o UID: " + uid);
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Erro ao recuperar dados do Firestore", e);
            return null;
        }

        if (cpf == null) {
            logger.warning("CPF não encontrado para o UID: " + uid);
            return null;
        }

        try{
            Playwright playwright = Playwright.create();
            Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            page.navigate("https://sig.ifrs.edu.br/sigaa/verTelaLogin.do");
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.fill("input[name='user.login']", cpf);
            page.fill("input[name='user.senha']", senha);
            page.click("input[type='submit']");

            page.waitForLoadState(LoadState.NETWORKIDLE);

            boolean loginError = page.isVisible("center:has-text(\"Usuário e/ou senha inválidos\")");

            if (loginError) {
                throw new UnauthorizedException("Usuário e/ou senha inválidos");
            } else {
                // Coleta os dados do perfil do usuário
                page.waitForSelector(".info-docente .nome");

                String nomeDocente = page.textContent(".info-docente .nome").trim();
                String matricula = page.textContent("td:has-text(\"Matrícula:\") + td").trim();
                String curso = page.textContent("td:has-text(\"Curso:\") + td").replaceAll("\\s+", " ").trim();
                String nivel = page.textContent("td:has-text(\"Nível:\") + td").trim();
                String status = page.textContent("td:has-text(\"Status:\") + td").trim();
                String anoIngresso = page.textContent("td:has-text(\"Entrada:\") + td").trim();
                String imgSrc = page.locator("#perfil-docente .foto img").getAttribute("src");
                logger.info("imgSrc: " + imgSrc);

                dadosUsuario = new Perfil(
                        nomeDocente != null ? nomeDocente : "Nome não disponível",
                        matricula != null ? matricula : "Matrícula não disponível",
                        cpf,
                        curso != null ? curso : "Curso não disponível",
                        nivel != null ? nivel : "Nível não disponível",
                        status != null ? status : "Status não disponível",
                        anoIngresso != null ? anoIngresso : "Ano de ingresso não disponível",
                        email,
                        imgSrc != null ? imgSrc : ""
                );

                //Fazer o download da foto e enviar para o bucket
                byte[] imgBytes = page.locator("#perfil-docente .foto img").screenshot();
                storage.bucket().create("perfil/" + uid + ".jpg", imgBytes);

                // Atualiza o Firestore com os novos dados
                ApiFuture<WriteResult> writeResult = db.collection("usuarios").document(uid).set(dadosUsuario);
                try {
                    writeResult.get();
                    logger.info("Dados do perfil atualizados no Firestore para o UID: " + uid);
                } catch (InterruptedException | ExecutionException e) {
                    logger.log(Level.SEVERE, "Erro ao atualizar dados no Firestore", e);
                }

                context.clearCookies();
                browser.close();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao obter dados do usuário", e);
        }
        return dadosUsuario;
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

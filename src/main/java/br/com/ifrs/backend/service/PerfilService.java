package br.com.ifrs.backend.service;

import br.com.ifrs.backend.exception.UnauthorizedException;
import br.com.ifrs.backend.model.Perfil;
import br.com.ifrs.backend.utils.FirestoreUtils;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
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
    private final FirestoreUtils firestoreUtils = new FirestoreUtils();
    private final StorageClient storage = StorageClient.getInstance();

    public Perfil obterDadosUsuario(String uid, String senha) {
        String cpf = firestoreUtils.getCpfFromFirestore(uid);
        String email = firestoreUtils.getEmailFromFirestore(uid);
        Perfil perfil = null;

        if (cpf == null) {
            logger.warning("CPF não encontrado para o UID: " + uid);
            return null;
        }

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            if (performLogin(page, cpf, senha)) {
                perfil = coletarDadosPerfil(page, cpf, email);
                atualizarPerfilNoFirestore(uid, perfil);
                enviarFotoAoStorage(uid);
            } else {
                logger.severe("Falha ao realizar login no SIGAA");
            }

            context.clearCookies();
            browser.close();
        } catch (UnauthorizedException e) {
            logger.warning("Erro de autorização: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao obter dados do usuário", e);
        }
        return perfil;
    }

    private boolean performLogin(Page page, String cpf, String senha) {
        try {
            page.navigate("https://sig.ifrs.edu.br/sigaa/verTelaLogin.do");
            page.fill("input[name='user.login']", cpf);
            page.fill("input[name='user.senha']", senha);
            page.click("input[type='submit']");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            return !page.isVisible("center:has-text(\"Usuário e/ou senha inválidos\")");
        } catch (PlaywrightException e) {
            logger.log(Level.SEVERE, "Erro ao realizar login no SIGAA", e);
            return false;
        }
    }

    private Perfil coletarDadosPerfil(Page page, String cpf, String email) {
        try {
            page.waitForSelector(".info-docente .nome");

            String nomeDocente = page.textContent(".info-docente .nome").trim();
            String matricula = page.textContent("td:has-text(\"Matrícula:\") + td").trim();
            String curso = page.textContent("td:has-text(\"Curso:\") + td").replaceAll("\\s+", " ").trim();
            String nivel = page.textContent("td:has-text(\"Nível:\") + td").trim();
            String status = page.textContent("td:has-text(\"Status:\") + td").trim();
            String anoIngresso = page.textContent("td:has-text(\"Entrada:\") + td").trim();
            String imgSrc = page.locator("#perfil-docente .foto img").getAttribute("src");

            return new Perfil(
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
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao coletar dados do perfil", e);
            return null;
        }
    }

    private void atualizarPerfilNoFirestore(String uid, Perfil perfil) {
        try {
            ApiFuture<WriteResult> writeResult = FirestoreClient.getFirestore()
                    .collection("usuarios")
                    .document(uid)
                    .set(perfil);
            writeResult.get();
            logger.info("Dados do perfil atualizados no Firestore para o UID: " + uid);
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Erro ao atualizar dados no Firestore", e);
            Thread.currentThread().interrupt(); // Restaurando o status de interrupção
        }
    }

    private void enviarFotoAoStorage(String uid) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            byte[] imgBytes = page.locator("#perfil-docente .foto img").screenshot();
            storage.bucket().create("perfil/" + uid + ".jpg", imgBytes);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao enviar foto ao Storage", e);
        }
    }
}

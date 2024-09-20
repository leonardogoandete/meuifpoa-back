package br.com.ifrs.backend.service;

import br.com.ifrs.backend.exception.UnauthorizedException;
import br.com.ifrs.backend.utils.FirestoreUtils;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class DocumentoService {
    private static final Logger logger = Logger.getLogger(DocumentoService.class.getName());
    private final Firestore db = FirestoreClient.getFirestore();
    private final FirestoreUtils firestoreUtils = new FirestoreUtils();

    public String downloadPdfAsBase64(String uid,String tipo, String senha) throws IOException {
        String cpf = firestoreUtils.getCpfFromFirestore(uid);

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
            //Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            if (!performLogin(page, cpf, senha)) {
                browser.close();
                throw new UnauthorizedException("Falha ao realizar login no SIGAA");
            }

            page.hover("span.ThemeOfficeMainFolderText:has-text('Ensino')");

            page.waitForSelector("div.ThemeOfficeSubMenu#cmSubMenuID1", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));

            Download download = page.waitForDownload(() -> {
                logger.log(Level.INFO,"tipo: {0}", tipo);
                switch (tipo) {
                    case "historico" ->
                            page.click("div.ThemeOfficeSubMenu#cmSubMenuID1 tr.ThemeOfficeMenuItem:has(td.ThemeOfficeMenuItemText:has-text('Emitir Histórico'))");
                    case "historicoEmentas" ->
                            page.click("div.ThemeOfficeSubMenu#cmSubMenuID1 tr.ThemeOfficeMenuItem:has(td.ThemeOfficeMenuItemText:has-text('Emitir Histórico com Ementas'))");
                    case "declaracaoVinculo" ->
                            page.click("div.ThemeOfficeSubMenu#cmSubMenuID1 tr.ThemeOfficeMenuItem:has(td.ThemeOfficeMenuItemText:has-text('Emitir Declaração de Vínculo'))");
                    case "atestadoMatricula" ->
                        page.click("div.ThemeOfficeSubMenu#cmSubMenuID1 tr.ThemeOfficeMenuItem:has(td.ThemeOfficeMenuItemText:has-text('Emitir Atestado de Matrícula'))");
                    default -> throw new IllegalArgumentException("Tipo de documento inválido");
                }
            });

            String downloadPath = download.path().toString();

            // Ler o arquivo PDF como byte array
            byte[] pdfBytes = Files.readAllBytes(Paths.get(downloadPath));

            // Converter para base64
            String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
            //savePdfToFirestore(uid, base64Pdf);

            browser.close();
            logger.log(Level.INFO,"Baixando documento");
            return base64Pdf;
        } catch (UnauthorizedException e) {
            logger.log(Level.SEVERE, "Erro de autenticação durante a sincronização", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao sincronizar com o SIGAA", e);
            throw new RuntimeException("Erro ao sincronizar com o SIGAA", e);
        }

    }

    private void savePdfToFirestore(String uid,String docPdf) {
            try {
                ApiFuture<WriteResult> result = db.collection("notas")
                        .document(uid)
                        .collection("disciplinas")
                        .document("historico")
                        .set(Map.of("pdfbase64", docPdf));
                result.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.log(Level.SEVERE, "Erro ao salvar nota no Firestore", e);
                Thread.currentThread().interrupt(); // Restaurando o status de interrupção
            }
        }

    private boolean performLogin(Page page, String cpf, String senha) {
        try {
            page.navigate("https://sig.ifrs.edu.br/sigaa/verTelaLogin.do");
            page.fill("input[name='user.login']", cpf);
            page.fill("input[name='user.senha']", senha);
            page.click("input[type='submit']");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            return !page.isVisible("center:has-text(\"Usuário e/ou senha inválidos\")");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao realizar login no SIGAA", e);
            return false;
        }
    }
}

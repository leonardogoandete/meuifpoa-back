package br.com.ifrs.backend.service;

import br.com.ifrs.backend.model.Notas;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class DocumentoService {
    private static final Logger logger = Logger.getLogger(DocumentoService.class.getName());
    private final Firestore db = FirestoreClient.getFirestore();

    public String downloadPdfAsBase64(String uid, String senha) throws IOException {
        String cpf = getCpfFromFirestore(uid);

        Playwright playwright = Playwright.create();
            Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            page.navigate("https://sig.ifrs.edu.br/sigaa/verTelaLogin.do");
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.fill("input[name='user.login']", cpf);
            page.fill("input[name='user.senha']", senha);
            page.click("input[type='submit']");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            page.hover("span.ThemeOfficeMainFolderText:has-text('Ensino')");

            page.waitForSelector("div.ThemeOfficeSubMenu#cmSubMenuID1", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));

            Download download = page.waitForDownload(() -> {
                page.click("div.ThemeOfficeSubMenu#cmSubMenuID1 tr.ThemeOfficeMenuItem:has(td.ThemeOfficeMenuItemText:has-text('Emitir Histórico'))");
            });

            String downloadPath = download.path().toString();
            //System.out.println("PDF baixado em: " + downloadPath);

            // Ler o arquivo PDF como byte array
            byte[] pdfBytes = Files.readAllBytes(Paths.get(downloadPath));

            // Converter para base64
            String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
            savePdfToFirestore(uid, base64Pdf);
            browser.close();
            logger.log(Level.INFO,"Baixando documento");
            return base64Pdf;

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
            Thread.currentThread().interrupt(); // Restaurando o status de interrupção
        }
        return null;
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
}

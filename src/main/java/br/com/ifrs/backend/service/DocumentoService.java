package br.com.ifrs.backend.service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class DocumentoService {
    private static final Logger logger = Logger.getLogger(DocumentoService.class.getName());

    public String downloadPdfAsBase64() throws IOException {
        Playwright playwright = Playwright.create();
            Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            page.navigate("https://sig.ifrs.edu.br/sigaa/verTelaLogin.do");
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.fill("input[name='user.login']", "");
            page.fill("input[name='user.senha']", "");
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

            browser.close();
            logger.log(Level.INFO,"Baixando documento");
            return base64Pdf;

    }
}

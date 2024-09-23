package br.com.ifrs.backend;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MenuNavigation {
    public static void main(String[] args) {
//            Playwright playwright = Playwright.create();
//            Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
//            BrowserContext context = browser.newContext();
//
//            Page page = context.newPage();
//
//            // Navegar até a página de login e fazer login
//            page.navigate("https://sig.ifrs.edu.br/sigaa/verTelaLogin.do");
//            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
//            page.fill("input[name='user.login']", "18035208764");
//            page.fill("input[name='user.senha']", "!Go@ndet3909260");
//            page.click("input[type='submit']");
//
//            page.waitForLoadState(LoadState.NETWORKIDLE);

//            // Passar o mouse sobre o menu principal "Ensino" para exibir o submenu
//            page.hover("span.ThemeOfficeMainFolderText:has-text('Ensino')");
//
//            // Esperar o submenu aparecer
//            page.waitForSelector("div.ThemeOfficeSubMenu#cmSubMenuID1", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
//
//            // Captura o evento de download
//            Download download = page.waitForDownload(() -> {
//                page.click("div.ThemeOfficeSubMenu#cmSubMenuID1 tr.ThemeOfficeMenuItem:has(td.ThemeOfficeMenuItemText:has-text('Emitir Histórico'))");
//            });
//
//            // Aguardar até que o download esteja concluído
//            String downloadPath = download.path().toString();
//            System.out.println("PDF baixado em: " + downloadPath);

//            // Mover o arquivo para um diretório específico, se necessário
//            String targetPath = "/home/";
//            Files.move(Paths.get(downloadPath), Paths.get(targetPath));
//            System.out.println("PDF salvo em: " + targetPath);
    }
}

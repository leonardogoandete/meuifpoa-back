package br.com.ifrs.backend.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import jakarta.enterprise.context.ApplicationScoped;
import br.com.ifrs.backend.model.Login;
import br.com.ifrs.backend.model.Notas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class NotasService {

    private static final Logger logger = Logger.getLogger(NotasService.class.getName());
    private final Firestore db = FirestoreClient.getFirestore();

    public List<Notas> obterNotas(String uid, Login login) {
        List<Notas> notas = new ArrayList<>();
        String cpf = getCpfFromFirestore(uid);

        if (cpf == null) {
            logger.warning("CPF não encontrado para o UID: " + uid);
            return notas;
        }

        logger.info("Consultando notas no SIGAA com o CPF: " + cpf);

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            if (performLogin(page, cpf, login.senha())) {
                notas = scrapeNotasFromPage(page);
                saveNotasToFirestore(uid, notas);
            } else {
                logger.severe("Falha ao realizar login no SIGAA");
            }
            context.clearCookies();
            browser.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao consultar notas", e);
        }

        return notas;
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

    private boolean performLogin(Page page, String cpf, String senha) {
        try {
            page.navigate("https://sig.ifrs.edu.br/sigaa/verTelaLogin.do");
            page.fill("input[name='user.login']", cpf);
            page.fill("input[name='user.senha']", senha);
            page.click("input[type='submit']");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            boolean loginError = page.isVisible("center:has-text(\"Usuário e/ou senha inválidos\")");
            return !loginError;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao realizar login", e);
            return false;
        }
    }

    private List<Notas> scrapeNotasFromPage(Page page) {
        List<Notas> notas = new ArrayList<>();
        try {
            page.click("td.ThemeOfficeMainItem:nth-child(1)");
            page.waitForSelector("#cmSubMenuID1");
            page.click("tr.ThemeOfficeMenuItem:nth-child(1)");
            page.waitForSelector("table.tabelaRelatorio");

            List<ElementHandle> tabelas = page.querySelectorAll("table.tabelaRelatorio");
            for (ElementHandle tabela : tabelas) {
                List<ElementHandle> linhas = tabela.querySelectorAll("tbody tr");
                for (ElementHandle linha : linhas) {
                    Notas nota = parseNotaFromElement(linha);
                    if (nota != null) {
                        notas.add(nota);
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao extrair notas da página", e);
        }
        return notas;
    }

    private Notas parseNotaFromElement(ElementHandle linha) {
        try {
            String codigo = linha.querySelector("td:nth-child(1)").innerText();
            String disciplina = linha.querySelector("td:nth-child(2)").innerText();
            String unidade1 = linha.querySelector("td:nth-child(3)").innerText();
            String unidade2 = linha.querySelector("td:nth-child(4)").innerText();
            String recuperacao = linha.querySelector("td:nth-child(5)").innerText();
            String resultado = linha.querySelector("td:nth-child(6)").innerText();
            String faltas = linha.querySelector("td:nth-child(7)").innerText();
            String situacao = linha.querySelector("td:nth-child(8)").innerText();

            return new Notas(codigo, disciplina, unidade1, unidade2, recuperacao, resultado, faltas, situacao);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao parsear nota de um elemento", e);
            return null;
        }
    }

    private void saveNotasToFirestore(String uid, List<Notas> notas) {
        for (Notas nota : notas) {
            ApiFuture<WriteResult> result = db.collection("notas")
                    .document(uid)
                    .collection("disciplinas")
                    .document(nota.getCodigoDisciplina())
                    .set(nota);

            try {
                result.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.log(Level.SEVERE, "Erro ao salvar nota no Firestore", e);
            }
        }
    }
}

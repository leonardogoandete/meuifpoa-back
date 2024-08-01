package br.com.ifrs.backend.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import jakarta.enterprise.context.ApplicationScoped;
import br.com.ifrs.backend.model.Login;
import br.com.ifrs.backend.model.Notas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class NotasService {

    private static final Logger logger = Logger.getLogger(NotasService.class.getName());
    private final RedisService redisService;
    private final Firestore db = FirestoreClient.getFirestore();

    public NotasService() {
        this.redisService = new RedisService();
    }

    public List<Notas> obterNotas(String uid, Login login) {
        List<Notas> notas = new ArrayList<>();

        logger.info("Consultando notas no SIGAA");

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            page.navigate("https://sig.ifrs.edu.br/sigaa/verTelaLogin.do");

            page.fill("input[name='user.login']", login.login());
            page.fill("input[name='user.senha']", login.senha());

            page.click("input[type='submit']");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            boolean loginError = page.isVisible("center:has-text(\"Usuário e/ou senha inválidos\")");

            if (loginError) {
                throw new RuntimeException("Usuário e/ou senha inválidos");
            } else {
                String redisValue = redisService.getValue(login.login() + "-notas");
                if (redisValue != null) {
                    logger.info("Valor " + login.login() + " recuperado do Redis");
                    Gson gson = new Gson();
                    Notas[] notasArray = gson.fromJson(redisValue, Notas[].class);
                    notas.addAll(Arrays.asList(notasArray));
                } else {
                    logger.info("Valor " + login.login() + " não encontrado no Redis");
                    page.click("td.ThemeOfficeMainItem:nth-child(1)");
                    page.waitForSelector("#cmSubMenuID1");
                    page.click("tr.ThemeOfficeMenuItem:nth-child(1)");
                    page.waitForSelector("table.tabelaRelatorio");

                    List<ElementHandle> tabelas = page.querySelectorAll("table.tabelaRelatorio");
                    for (ElementHandle tabela : tabelas) {
                        List<ElementHandle> linhas = tabela.querySelectorAll("tbody tr");
                        for (ElementHandle linha : linhas) {
                            String codigo = linha.querySelector("td:nth-child(1)").innerText();
                            String disciplina = linha.querySelector("td:nth-child(2)").innerText();
                            String unidade1 = linha.querySelector("td:nth-child(3)").innerText();
                            String unidade2 = linha.querySelector("td:nth-child(4)").innerText();
                            String recuperacao = linha.querySelector("td:nth-child(5)").innerText();
                            String resultado = linha.querySelector("td:nth-child(6)").innerText();
                            String faltas = linha.querySelector("td:nth-child(7)").innerText();
                            String situacao = linha.querySelector("td:nth-child(8)").innerText();

                            Notas nota = new Notas(codigo, disciplina, unidade1, unidade2, recuperacao, resultado, faltas, situacao);
                            notas.add(nota);
                            //db.collection("notas").document(uid).set();
                        }
                    }

                    Gson gson = new Gson();
                    String json = gson.toJson(notas);
                    redisService.setKey(login.login() + "-notas", json);
                }
            }
            context.clearCookies();
            browser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Salvar cada nota como um documento separado
        for (Notas nota : notas) {
            ApiFuture<WriteResult> result = db.collection("notas").document(uid).collection("disciplinas").document(nota.getCodigoDisciplina()).set(nota);
            try {
                result.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.log(Level.SEVERE, "Erro ao salvar nota no Firestore", e);
            }
        }
        return notas;
    }
}

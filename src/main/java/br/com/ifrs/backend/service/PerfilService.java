package br.com.ifrs.backend.service;

import br.com.ifrs.backend.model.Perfil;
import com.google.gson.Gson;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.logging.Logger;

@ApplicationScoped
public class PerfilService {
    private static final Logger logger = Logger.getLogger(PerfilService.class.getName());
    private final RedisService redisService;
    private String cpf = "";
    private String senha = "";


    public PerfilService() {
        this.redisService = new RedisService();
    }

    public Perfil obterDadosUsuario(String uid) {
        Perfil dadosUsuario = null;

        try (Playwright playwright = Playwright.create()) {
            
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
                    throw new RuntimeException("Usuário e/ou senha inválidos");
                } else {
                    String redisValue = redisService.getValue(cpf+"-dados-usuario");
                    if (redisValue != null) {
                        logger.info("Dados usuario "+ cpf +" recuperado do Redis");
                        Gson gson = new Gson();
                        dadosUsuario = gson.fromJson(redisValue, Perfil.class);
                    } else {
                        logger.info("Valor " + cpf + " não encontrado no Redis");
                        page.waitForSelector(".info-docente .nome");

                        String nomeDocente = page.textContent(".info-docente .nome").trim();
                        String matricula = page.textContent("td:has-text(\"Matrícula:\") + td").trim();
                        String curso = page.textContent("td:has-text(\"Curso:\") + td").replaceAll("\\s+", " ").trim();
                        String nivel = page.textContent("td:has-text(\"Nível:\") + td").trim();
                        String status = page.textContent("td:has-text(\"Status:\") + td").trim();
                        String entrada = page.textContent("td:has-text(\"Entrada:\") + td").trim();
                        String imgSrc = page.locator("#perfil-docente .foto img").getAttribute("src");
                        logger.info("imgSrc: " + imgSrc);
                        dadosUsuario = new Perfil(nomeDocente, matricula, curso, nivel, status, entrada);
                        Gson gson = new Gson();
                        String json = gson.toJson(dadosUsuario);
                        redisService.setKey(cpf+"-dados-usuario", json);

                        context.clearCookies();
                        browser.close();
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dadosUsuario;
    }
}

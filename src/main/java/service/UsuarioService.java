package service;

import com.google.gson.Gson;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import jakarta.enterprise.context.ApplicationScoped;
import model.Login;
import model.DadosUsuario;

import java.util.logging.Logger;

@ApplicationScoped
public class UsuarioService {
    private static final Logger logger = Logger.getLogger(UsuarioService.class.getName());
    private RedisService redisService;

    public UsuarioService() {
        this.redisService = new RedisService();
    }

    public DadosUsuario obterDadosUsuario(Login login) {
        DadosUsuario dadosUsuario = null;
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.firefox().launch();
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            page.navigate("https://sig.ifrs.edu.br/sigaa/verTelaLogin.do");
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.fill("input[name='user.login']", login.login());
            page.fill("input[name='user.senha']", login.senha());
            page.click("input[type='submit']");

            // Esperar até que a página seja completamente carregada
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Verificar se o elemento de erro de login está presente
            boolean loginError = page.isVisible("center:has-text(\"Usuário e/ou senha inválidos\")");

            if (loginError) {
                // Ocorreu um erro de login
                throw new RuntimeException("Usuário e/ou senha inválidos");
            } else {
                String redisValue = redisService.getValue(login.login()+"-dados-usuario");
                if (redisValue != null) {
                    logger.info("Dados usuario "+ login.login() +" recuperado do Redis");
                    Gson gson = new Gson();
                    dadosUsuario = gson.fromJson(redisValue, DadosUsuario.class);

                }else {
                    logger.info("Valor " + login.login() + " não encontrado no Redis");
                    page.waitForSelector(".info-docente .nome");

                    String nomeDocente = page.textContent(".info-docente .nome").trim();
                    String matricula = page.textContent("td:has-text(\"Matrícula:\") + td").trim();
                    String curso = page.textContent("td:has-text(\"Curso:\") + td").replaceAll("\\s+", " ").trim();
                    String nivel = page.textContent("td:has-text(\"Nível:\") + td").trim();
                    String status = page.textContent("td:has-text(\"Status:\") + td").trim();
                    String entrada = page.textContent("td:has-text(\"Entrada:\") + td").trim();

                    dadosUsuario = new DadosUsuario(nomeDocente, matricula, curso, nivel, status, entrada);
                    Gson gson = new Gson();
                    String json = gson.toJson(dadosUsuario);
                    redisService.setKey(login.login()+"-dados-usuario", json);

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

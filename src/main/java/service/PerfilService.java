package service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.google.gson.Gson;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import jakarta.enterprise.context.ApplicationScoped;
import model.Login;
import model.Perfil;
import org.hibernate.boot.model.relational.Database;

import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class PerfilService {
    private static final Logger logger = Logger.getLogger(PerfilService.class.getName());
    private final RedisService redisService;
    String senha = "";

    public PerfilService() {
        this.redisService = new RedisService();
    }

    //public Perfil obterDadosUsuario(Login login) {
    public Perfil obterDadosUsuario(String cpf) {
        Perfil dadosUsuario = null;

        try (Playwright playwright = Playwright.create()) {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("usuarios");

        mDatabase.child(cpf).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Verifica se o nó com o CPF fornecido existe
                if (dataSnapshot.exists()) {
                    // Obtém os valores do nó do usuário
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String nome = dataSnapshot.child("nome").getValue(String.class);
                    senha = dataSnapshot.child("senha").getValue(String.class);




                    // Aqui você pode fazer o que quiser com os valores recuperados
                    logger.log(Level.INFO, "CPF: {0}, Email: {1}, Nome: {2}, Senha: {3}", new Object[]{cpf, email, nome, senha});
                } else {
                    logger.log(Level.WARNING, "Nenhum usuário encontrado com o CPF fornecido: {0}", cpf);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.log(Level.WARNING, "Erro ao consultar o usuário na base de dados: " + databaseError.getMessage());
            }
        });


            Login login = new Login(cpf,senha);
            Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            page.navigate("https://sig.ifrs.edu.br/sigaa/verTelaLogin.do");
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.fill("input[name='user.login']", cpf);
            page.fill("input[name='user.senha']", senha);
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
                    dadosUsuario = gson.fromJson(redisValue, Perfil.class);

                }else {
                    logger.info("Valor " + login.login() + " não encontrado no Redis");
                    page.waitForSelector(".info-docente .nome");

                    String nomeDocente = page.textContent(".info-docente .nome").trim();
                    String matricula = page.textContent("td:has-text(\"Matrícula:\") + td").trim();
                    String curso = page.textContent("td:has-text(\"Curso:\") + td").replaceAll("\\s+", " ").trim();
                    String nivel = page.textContent("td:has-text(\"Nível:\") + td").trim();
                    String status = page.textContent("td:has-text(\"Status:\") + td").trim();
                    String entrada = page.textContent("td:has-text(\"Entrada:\") + td").trim();

                    dadosUsuario = new Perfil(nomeDocente, matricula, curso, nivel, status, entrada);
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

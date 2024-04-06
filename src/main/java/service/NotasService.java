package service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import jakarta.enterprise.context.ApplicationScoped;
import model.Login;
import model.Notas;
import model.Usuario;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class NotasService {

    public List<Notas> obterNotas(Login login){
        List<Notas> notas = new ArrayList<>();
        try (Playwright playwright = Playwright.create()) {
            // Inicia o navegador
            //Sem exibir
            Browser browser = playwright.firefox().launch();
            //Para exibir o navegador
            //Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
            BrowserContext context = browser.newContext();
            // Abre uma nova página
            Page page = context.newPage();
            // Navega até a página de login do SIGAA e aguarda o carregamento completo
            page.navigate("https://sig.ifrs.edu.br/sigaa/verTelaLogin.do");

            // Preenche o formulário de login
            page.fill("input[name='user.login']", login.login());
            page.fill("input[name='user.senha']", login.senha());

            // Clica no botão "Entrar"
            page.click("input[type='submit']");

            // Clica na opção "Ensino" no menu
            page.click("td.ThemeOfficeMainItem:nth-child(1)");

            // Aguarda o submenu "Ensino" aparecer
            page.waitForSelector("#cmSubMenuID1");

            // Clica na opção "Consultar Minhas Notas" no submenu "Ensino"
            page.click("tr.ThemeOfficeMenuItem:nth-child(1)");

            // Aguarda até que a tabela esteja carregada na página
            page.waitForSelector("table.tabelaRelatorio");

            // Encontra todas as tabelas na página
            List<ElementHandle> tabelas = page.querySelectorAll("table.tabelaRelatorio");

            // Itera sobre cada tabela
            for (ElementHandle tabela : tabelas) {
                // Verifica se a tabela é referente ao ano de 2024.1
                String caption = tabela.querySelector("caption").innerText();
                //if (caption.equals("2024.1")) {
                    // Encontra todas as linhas da tabela
                    List<ElementHandle> linhas = tabela.querySelectorAll("tbody tr");
                    // Itera sobre cada linha
                    for (ElementHandle linha : linhas) {
                        // Extrai e exibe o código, a disciplina, as notas e a situação
                        String codigo = linha.querySelector("td:nth-child(1)").innerText();
                        String disciplina = linha.querySelector("td:nth-child(2)").innerText();
                        String unidade1 = linha.querySelector("td:nth-child(3)").innerText();
                        String unidade2 = linha.querySelector("td:nth-child(4)").innerText();
                        String recuperacao = linha.querySelector("td:nth-child(5)").innerText();
                        String resultado = linha.querySelector("td:nth-child(6)").innerText();
                        String faltas = linha.querySelector("td:nth-child(7)").innerText();
                        String situacao = linha.querySelector("td:nth-child(8)").innerText();

                        notas.add(new Notas(codigo,disciplina,unidade1,unidade2,recuperacao,resultado,faltas,situacao));

                    }
                //}
            }

            //Limpando cookies
            context.clearCookies();
            // Fecha o navegador
            browser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notas;
    }

    public Usuario obterDadosUsuario(Login login) {
        Usuario u = null;
        try (Playwright playwright = Playwright.create()) {
            //Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
            //Sem exibir
            Browser browser = playwright.firefox().launch();
            //Page page = browser.newPage();
            BrowserContext context = browser.newContext();
            // Abre uma nova página
            Page page = context.newPage();

            page.navigate("https://sig.ifrs.edu.br/sigaa/verTelaLogin.do");

            // Espera até que os seletores estejam prontos para interação
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);

            // Preenche o formulário de login e aguarda o redirecionamento para a página principal
            page.fill("input[name='user.login']", login.login());
            page.fill("input[name='user.senha']", login.senha());
            page.click("input[type='submit']");

            // Espera até que os dados do usuário sejam carregados
            page.waitForSelector(".info-docente .nome");

            // Obtém dados do usuário
            String nomeDocente = page.textContent(".info-docente .nome").trim();
            String matricula = page.textContent("td:has-text(\"Matrícula:\") + td").trim();
            String curso = page.textContent("td:has-text(\"Curso:\") + td").replaceAll("\\s+", " ").trim();
            String nivel = page.textContent("td:has-text(\"Nível:\") + td").trim();
            String status = page.textContent("td:has-text(\"Status:\") + td").trim();
            String entrada = page.textContent("td:has-text(\"Entrada:\") + td").trim();

            // Cria o objeto de usuário
            u = new Usuario(nomeDocente, matricula, curso, nivel, status, entrada);
            context.clearCookies();
            // Fecha o navegador
            browser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }
}

package br.com.ifrs.backend.service;

import br.com.ifrs.backend.exception.UnauthorizedException;
import br.com.ifrs.backend.model.Notas;
import br.com.ifrs.backend.model.Perfil;
import br.com.ifrs.backend.utils.FirestoreUtils;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class SyncService {

    private static final Logger logger = Logger.getLogger(SyncService.class.getName());
    private final Firestore db = FirestoreClient.getFirestore();
    private final FirestoreUtils firestoreUtils = new FirestoreUtils();
    private final StorageClient storage = StorageClient.getInstance();

    public void sincronizar(String uid, String senha) {
        String cpf = firestoreUtils.getCpfFromFirestore(uid);
        if (cpf == null) {
            logger.warning("CPF não encontrado para o UID: " + uid);
            throw new UnauthorizedException("CPF não encontrado para o UID: " + uid);
        }

        logger.info("Iniciando sincronização com o SIGAA para o CPF: " + cpf);

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // Perform login
            if (!performLogin(page, cpf, senha)) {
                logger.severe("Falha ao realizar login no SIGAA");
                throw new UnauthorizedException("Falha ao realizar login no SIGAA");
            }

            // Process profile
            Perfil perfil = coletarDadosPerfil(page, cpf);
            atualizarPerfilNoFirestore(uid, perfil);
            enviarFotoAoStorage(uid, page);

            // Process notes
            List<Notas> notas = scrapeNotasFromPage(page);
            saveNotasToFirestore(uid, notas);

            context.clearCookies();
            browser.close();
        } catch (UnauthorizedException e) {
            logger.log(Level.SEVERE, "Erro de autenticação durante a sincronização", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao sincronizar com o SIGAA", e);
            throw new RuntimeException("Erro ao sincronizar com o SIGAA", e);
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

    private List<Notas> scrapeNotasFromPage(Page page) {
        List<Notas> notas = new ArrayList<>();
        try {
            page.click("td.ThemeOfficeMainItem:nth-child(1)");
            page.waitForSelector("#cmSubMenuID1");
            page.click("tr.ThemeOfficeMenuItem:nth-child(1)");
            page.waitForSelector("table.tabelaRelatorio");

            List<ElementHandle> tabelas = page.querySelectorAll("table.tabelaRelatorio");
            for (ElementHandle tabela : tabelas) {
                notas.addAll(parseNotasFromTable(tabela));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao extrair notas da página", e);
        }
        return notas;
    }

    private List<Notas> parseNotasFromTable(ElementHandle tabela) {
        List<Notas> notas = new ArrayList<>();
        List<ElementHandle> linhas = tabela.querySelectorAll("tbody tr");

        for (ElementHandle linha : linhas) {
            Notas nota = parseNotaFromElement(linha);
            if (nota != null) {
                notas.add(nota);
            }
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
            try {
                ApiFuture<WriteResult> result = db.collection("notas")
                        .document(uid)
                        .collection("disciplinas")
                        .document(nota.getCodigoDisciplina())
                        .set(nota);
                result.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.log(Level.SEVERE, "Erro ao salvar nota no Firestore", e);
                Thread.currentThread().interrupt(); // Restaurando o status de interrupção
            }
        }
    }

    private Perfil coletarDadosPerfil(Page page, String cpf) {
        String email = firestoreUtils.getEmailFromFirestore(cpf); // Assuming email is obtained using cpf
        try {
            page.waitForSelector(".info-docente .nome");

            String nomeDocente = page.textContent(".info-docente .nome").trim();
            String matricula = page.textContent("td:has-text(\"Matrícula:\") + td").trim();
            String curso = page.textContent("td:has-text(\"Curso:\") + td").replaceAll("\\s+", " ").trim();
            String nivel = page.textContent("td:has-text(\"Nível:\") + td").trim();
            String status = page.textContent("td:has-text(\"Status:\") + td").trim();
            String anoIngresso = page.textContent("td:has-text(\"Entrada:\") + td").trim();
            String imgSrc = page.locator("#perfil-docente .foto img").getAttribute("src");

            return new Perfil(
                    nomeDocente != null ? nomeDocente : "Nome não disponível",
                    matricula != null ? matricula : "Matrícula não disponível",
                    cpf,
                    curso != null ? curso : "Curso não disponível",
                    nivel != null ? nivel : "Nível não disponível",
                    status != null ? status : "Status não disponível",
                    anoIngresso != null ? anoIngresso : "Ano de ingresso não disponível",
                    email,
                    imgSrc != null ? imgSrc : ""
            );
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao coletar dados do perfil", e);
            return null;
        }
    }

    private void atualizarPerfilNoFirestore(String uid, Perfil perfil) {
        try {
            ApiFuture<WriteResult> writeResult = db.collection("usuarios")
                    .document(uid)
                    .set(perfil);
            writeResult.get();
            logger.info("Dados do perfil atualizados no Firestore para o UID: " + uid);
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Erro ao atualizar dados no Firestore", e);
            Thread.currentThread().interrupt(); // Restaurando o status de interrupção
        }
    }

    private void enviarFotoAoStorage(String uid, Page page) {
        try {
            byte[] imgBytes = page.locator("#perfil-docente .foto img").screenshot();
            storage.bucket().create("perfil/" + uid + ".jpg", imgBytes);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao enviar foto ao Storage", e);
        }
    }
}

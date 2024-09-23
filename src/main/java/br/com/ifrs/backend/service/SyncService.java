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
import jakarta.enterprise.context.ApplicationScoped;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class SyncService {

    private static final Logger logger = Logger.getLogger(SyncService.class.getName());
    private final Firestore db = FirestoreClient.getFirestore();
    private final FirestoreUtils firestoreUtils = new FirestoreUtils();
    private final StorageClient storage = StorageClient.getInstance();

    // Mapa de cookies personalizado
    private final Map<String, List<Cookie>> cookieStore = new java.util.HashMap<>();

    private final OkHttpClient client;

    public SyncService() {
        this.client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<>();
                    }
                })
                .build();
    }

    public void sincronizar(String uid, String senha) throws IOException {
        String cpf = firestoreUtils.getCpfFromFirestore(uid);
        if (cpf == null) {
            logger.warning("CPF não encontrado para o UID: " + uid);
            throw new UnauthorizedException("CPF não encontrado para o UID: " + uid);
        }

        logger.info("Iniciando sincronização com o SIGAA para o CPF: " + cpf);

        try {
            // Realiza o login
            String loginUrl = "https://sig.ifrs.edu.br/sigaa/logar.do?dispatch=logOn";
            if (!performLogin(loginUrl, cpf, senha)) {
                logger.severe("Falha ao realizar login no SIGAA");
                throw new UnauthorizedException("Falha ao realizar login no SIGAA");
            }

            // Processar perfil
            Perfil perfil = coletarDadosPerfil(cpf);
            atualizarPerfilNoFirestore(uid, perfil);

            // Processar notas
            List<Notas> notas = obterNotas();
            saveNotasToFirestore(uid, notas);

        } finally {
            // Limpar os cookies ao final do processo
            limparCookies();
        }
    }

    private boolean performLogin(String loginUrl, String username, String password) throws IOException {
        FormBody formBody = new FormBody.Builder()
                .add("user.login", username)
                .add("user.senha", password)
                .build();

        Request postLoginRequest = new Request.Builder()
                .url(loginUrl)
                .post(formBody)
                .build();

        try (Response response = client.newCall(postLoginRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro ao realizar o login: " + response);
            }
            String responseBody = response.body().string();
            return !responseBody.contains("Usuário e/ou senha inválidos");
        }
    }

    private List<Notas> obterNotas() throws IOException {
        String postUrl = "https://sig.ifrs.edu.br/sigaa/portais/discente/discente.jsf";
        FormBody formBody = new FormBody.Builder()
                .add("menu:form_menu_discente", "menu:form_menu_discente")
                .add("id", "11278")
                .add("jscook_action", "menu_form_menu_discente_j_id_jsp_925609363_97_menu:A]#{ relatorioNotasAluno.gerarRelatorio }")
                .add("javax.faces.ViewState", "j_id1")
                .build();

        Request postRequest = new Request.Builder()
                .url(postUrl)
                .post(formBody)
                .build();

        try (Response response = client.newCall(postRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro ao realizar o POST para obter notas: " + response);
            }
            return parseNotasFromHtml(response.body().string());
        }
    }

    private List<Notas> parseNotasFromHtml(String html) {
        List<Notas> notas = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements tabelas = doc.select("table.tabelaRelatorio");

        for (Element tabela : tabelas) {
            Elements linhas = tabela.select("tbody tr");
            for (Element linha : linhas) {
                Notas nota = parseNotaFromElement(linha);
                if (nota != null) {
                    notas.add(nota);
                }
            }
        }

        return notas;
    }

    private Notas parseNotaFromElement(Element linha) {
        try {
            String codigo = linha.select("td:nth-child(1)").text();
            String disciplina = linha.select("td:nth-child(2)").text();
            String unidade1 = linha.select("td:nth-child(3)").text();
            String unidade2 = linha.select("td:nth-child(4)").text();
            String recuperacao = linha.select("td:nth-child(5)").text();
            String resultado = linha.select("td:nth-child(6)").text();
            String faltas = linha.select("td:nth-child(7)").text();
            String situacao = linha.select("td:nth-child(8)").text();

            return new Notas(codigo, disciplina, unidade1, unidade2, recuperacao, resultado, faltas, situacao);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao parsear nota de um elemento", e);
            return null;
        }
    }

    private Perfil coletarDadosPerfil(String cpf) throws IOException {
        String perfilUrl = "https://sig.ifrs.edu.br/sigaa/portais/discente/discente.jsf";
        Request getRequest = new Request.Builder()
                .url(perfilUrl)
                .get()
                .build();

        try (Response response = client.newCall(getRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro ao carregar a página de perfil: " + response);
            }
            return parsePerfilFromHtml(response.body().string(), cpf);
        }
    }

    private Perfil parsePerfilFromHtml(String html, String cpf) {
        Document doc = Jsoup.parse(html);
        String nomeDocente = doc.selectFirst(".info-docente .nome").text();
        String matricula = doc.selectFirst("td:contains(Matrícula:) + td").text();
        String curso = doc.selectFirst("td:contains(Curso:) + td").text();
        String nivel = doc.selectFirst("td:contains(Nível:) + td").text();
        String status = doc.selectFirst("td:contains(Status:) + td").text();
        String anoIngresso = doc.selectFirst("td:contains(Entrada:) + td").text();
        String chObrigatoriaPendente = doc.selectFirst("td:contains(CH. Obrigatória Pendente) + td").text();
        String chOptativaPendente = doc.selectFirst("td:contains(CH. Optativa Pendente) + td").text();
        String chTotalCurriculo = doc.selectFirst("td:contains(CH. Total Currículo) + td").text();
        String chComplementarPendente = doc.selectFirst("td:contains(CH. Complementar Pendente) + td").text();
        String imgSrc = doc.selectFirst("#perfil-docente .foto img").attr("src");

        String email = firestoreUtils.getEmailFromFirestore(cpf);
        String integralizado = calculaIntegralizacao(chObrigatoriaPendente, chOptativaPendente, chTotalCurriculo, chComplementarPendente);

        return new Perfil(
                nomeDocente,
                matricula,
                cpf,
                curso,
                nivel,
                status,
                anoIngresso,
                email,
                imgSrc,
                chObrigatoriaPendente,
                chOptativaPendente,
                chTotalCurriculo,
                chComplementarPendente,
                integralizado
        );
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
            Thread.currentThread().interrupt();
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
                Thread.currentThread().interrupt();
            }
        }
    }

    private String calculaIntegralizacao(String chObrigatoriaPendente, String chOptativaPendente, String chTotalCurriculo, String chComplementarPendente) {
        return String.format("%.0f", 100 - ((Float.parseFloat(chObrigatoriaPendente) + Float.parseFloat(chOptativaPendente) + Float.parseFloat(chComplementarPendente)) * 100 / Float.parseFloat(chTotalCurriculo)));
    }

    // Método para limpar os cookies ao final do processo
    private void limparCookies() {
        cookieStore.clear();
        logger.info("Cookies limpos após o processo.");
    }
}

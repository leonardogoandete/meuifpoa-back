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
import jakarta.inject.Inject;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class SyncService {

    private static final Logger logger = Logger.getLogger(SyncService.class.getName());

    private final Map<String, List<Cookie>> cookieStore = new HashMap<>();

    private final Firestore db = FirestoreClient.getFirestore();
    private final FirestoreUtils firestoreUtils = new FirestoreUtils();

    private final OkHttpClient client;
    private boolean sincronizado;

    public SyncService() {
        this.client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        return cookieStore.getOrDefault(url.host(), new ArrayList<>());
                    }
                })
                .build();
    }

    public boolean sincronizar(String uid, String senha) throws IOException {
        sincronizado = false; // Inicializa como não sincronizado
        String cpf = firestoreUtils.getCpfFromFirestore(uid);
        if (cpf == null) {
            logger.warning("CPF não encontrado para o UID: " + uid);
            throw new UnauthorizedException("CPF não encontrado para o UID: " + uid);
        }

        logger.info("Iniciando sincronização com o SIGAA para o CPF: " + cpf);

        try {
            if (!realizarLogin(cpf, senha)) {
                logger.severe("Falha ao realizar login no SIGAA");
                throw new UnauthorizedException("Falha ao realizar login no SIGAA");
            }

            Perfil perfil = coletarDadosPerfil(cpf);
            atualizarPerfilNoFirestore(uid, perfil);

            List<Notas> notas = obterNotas();
            saveNotasToFirestore(uid, notas);
            sincronizado = true;
        } finally {
            limparCookies();
        }
        return sincronizado;
    }

    private boolean realizarLogin(String username, String password) throws IOException {
        FormBody formBody = new FormBody.Builder()
                .add("user.login", username)
                .add("user.senha", password)
                .build();

        Request postLoginRequest = new Request.Builder()
                .url("https://sig.ifrs.edu.br/sigaa/logar.do?dispatch=logOn")
                .post(formBody)
                .build();

        try (Response response = client.newCall(postLoginRequest).execute()) {
            if (!response.isSuccessful()) {
                sincronizado = false;
                throw new IOException("Erro ao realizar o login: " + response);
            }
            String responseBody = response.body().string();
            return !responseBody.contains("Usuário e/ou senha inválidos");
        }
    }

    private List<Notas> obterNotas() throws IOException {
        Request postRequest = criarRequestParaNotas();

        try (Response response = client.newCall(postRequest).execute()) {
            if (!response.isSuccessful()) {
                sincronizado = false;
                throw new IOException("Erro ao realizar o POST para obter notas: " + response);
            }
            return parseNotasFromHtml(response.body().string());
        }
    }

    private Request criarRequestParaNotas() {
        FormBody formBody = new FormBody.Builder()
                .add("menu:form_menu_discente", "menu:form_menu_discente")
                .add("id", "11278")
                .add("jscook_action", "menu_form_menu_discente_j_id_jsp_925609363_97_menu:A]#{ relatorioNotasAluno.gerarRelatorio }")
                .add("javax.faces.ViewState", "j_id1")
                .build();

        return new Request.Builder()
                .url("https://sig.ifrs.edu.br/sigaa/portais/discente/discente.jsf")
                .post(formBody)
                .build();
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
            return new Notas(
                    linha.select("td:nth-child(1)").text(),
                    linha.select("td:nth-child(2)").text(),
                    linha.select("td:nth-child(3)").text(),
                    linha.select("td:nth-child(4)").text(),
                    linha.select("td:nth-child(5)").text(),
                    linha.select("td:nth-child(6)").text(),
                    linha.select("td:nth-child(7)").text(),
                    linha.select("td:nth-child(8)").text()
            );
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao parsear nota", e);
            return null;
        }
    }

    private Perfil coletarDadosPerfil(String cpf) throws IOException {
        Request getRequest = new Request.Builder()
                .url("https://sig.ifrs.edu.br/sigaa/portais/discente/discente.jsf")
                .get()
                .build();

        try (Response response = client.newCall(getRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro ao carregar a página de perfil: " + response);
            }
            return parsePerfilFromHtml(response.body().string(), cpf);
        }
    }

    private Perfil parsePerfilFromHtml(String html, String cpf) throws IOException {
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

        String fotoBase64 = baixarImagemEConverterParaBase64(imgSrc);
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
                fotoBase64,
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

    private String baixarImagemEConverterParaBase64(String imgUrl) throws IOException {
        Request request = new Request.Builder()
                .url(imgUrl)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro ao baixar a imagem: " + response);
            }

            byte[] imageBytes = response.body().bytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }

    private void limparCookies() {
        cookieStore.clear();
        logger.info("Cookies limpos após o processo.");
    }
}

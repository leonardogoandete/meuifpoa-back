package br.com.ifrs.meuifpoaback.service;

import br.com.ifrs.meuifpoaback.client.SigaaClient;
import br.com.ifrs.meuifpoaback.exception.UnauthorizedException;
import br.com.ifrs.meuifpoaback.model.*;
import br.com.ifrs.meuifpoaback.utils.FirestoreUtils;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import okhttp3.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Serviço responsável pela sincronização de dados com o SIGAA.
 */
@ApplicationScoped
public class SyncService {

    private static final Logger logger = Logger.getLogger(SyncService.class.getName());

    private final Map<String, List<Cookie>> cookieStore = new HashMap<>();

    private final Firestore db = FirestoreClient.getFirestore();
    private final FirestoreUtils firestoreUtils = new FirestoreUtils();

    private RespostaToken tokenAtual;
    private long tokenExpiracao;

    private final OkHttpClient client;
    private boolean sincronizado;
    private final List<Notas> notas = new ArrayList<>();

    @ConfigProperty(name = "sigaaApi.grant_type")
    String clientCredentials;

    @ConfigProperty(name = "sigaaApi.client_id")
    String clientId;

    @ConfigProperty(name = "sigaaApi.client_secret")
    String clientSecret;

    @Inject
    @RestClient
    SigaaClient sigaaClient;


    /**
     * Construtor do serviço de sincronização.
     * Inicializa o cliente HTTP com um gerenciador de cookies.
     */
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

    /**
     * Sincroniza os dados do perfil do usuário no SIGAA.
     *
     * @param uid   Identificador do usuário.
     * @param senha Senha do usuário.
     * @return Perfil do usuário sincronizado com os dados do SIGAA.
     * @throws IOException Em caso de erro de entrada/saída.
     */
    public Perfil sincronizar(String uid, String senha) throws IOException {
        sincronizado = false; // Inicializa como não sincronizado
        String email = firestoreUtils.getEmailFromFirestore(uid);
        String matricula = email.split("@")[0];

        // Obter o token de acesso
        ObterToken obterToken = new ObterToken(clientCredentials, clientId, clientSecret);
        // Obter o CPF da API do Sigaa
        RespostaToken token = null;

        try {
            token = sigaaClient.getToken(obterToken);
            logger.info("Token obtido: " + token.getAccessToken());
        } catch (WebApplicationException e) {
            String responseBody = e.getResponse().readEntity(String.class);
            logger.severe("Erro ao obter o token: " + responseBody);
            throw e;
        } catch (Exception e) {
            logger.severe("Erro inesperado ao obter o token: " + e);
            throw e;
        }

        Map<String, AlunoSigaa> alunoMap = sigaaClient.getAluno(matricula, "Bearer "+ token.getAccessToken());
        String cpf = alunoMap.values().iterator().next().getLogin();


        Perfil perfil;

        if (cpf == null) {
            logger.warning("CPF não encontrado para o matricula: " + matricula);
            throw new UnauthorizedException("CPF não encontrado para o matricula: " + matricula);
        }

        logger.info("Iniciando sincronização com o SIGAA para o CPF: " + cpf);

        try {
            if (!realizarLogin(cpf, senha)) {
                logger.severe("Falha ao realizar login no SIGAA");
                throw new UnauthorizedException("Falha ao realizar login no SIGAA");
            }

            perfil = coletarDadosPerfil(cpf,email);

            // Busque as notas existentes no Firestore
            ArrayList<Notas> notasExistentes = obterNotasExistentesDoFirestore(uid);

            // Obtenha as novas notas
            List<Notas> novasNotas = obterNotas();

            // Atualize a lista de notas no perfil, sem duplicação
            perfil.setNotas(atualizarNotasExistentes(notasExistentes, novasNotas));

            // Atualize o perfil no Firestore
            atualizarPerfilNoFirestore(uid, perfil);

        } catch (ExecutionException | InterruptedException e) {
            logger.log(Level.SEVERE, "Erro ao obter notas existentes do Firestore", e);
            throw new RuntimeException(e);
        } finally {
            limparCookies();
        }
        return perfil;
    }

    /**
     * Atualiza a lista de notas existentes com novas notas, evitando duplicações.
     *
     * @param notasExistentes Lista de notas existentes no Firestore.
     * @param novasNotas Lista de novas notas obtidas do SIGAA.
     * @return Lista atualizada de notas.
     */
    private ArrayList<Notas> atualizarNotasExistentes(List<Notas> notasExistentes, List<Notas> novasNotas) {
        Map<String, Notas> mapNotasExistentes = new HashMap<>();

        // Cria um mapa das notas existentes usando o código da disciplina como chave
        for (Notas nota : notasExistentes) {
            mapNotasExistentes.put(nota.getCodigoDisciplina(), nota);
        }

        // Atualiza ou adiciona novas notas ao mapa
        for (Notas novaNota : novasNotas) {
            mapNotasExistentes.put(novaNota.getCodigoDisciplina(), novaNota);  // Substitui ou adiciona
        }

        // Retorna a lista atualizada de notas
        return new ArrayList<>(mapNotasExistentes.values());
    }


    /**
     * Realiza o login no SIGAA.
     *
     * @param username Nome de usuário.
     * @param password Senha do usuário.
     * @return true se o login for bem-sucedido, false caso contrário.
     * @throws IOException Se ocorrer um erro de I/O.
     */
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
                logger.severe("Erro ao realizar o login: " + response);
                throw new IOException("Erro ao realizar o login: " + response);
            }
            String responseBody = response.body().string();
            return !responseBody.contains("Usuário e/ou senha inválidos");
        }
    }

    /**
     * Obtém as notas do usuário.
     *
     * @return Lista de notas.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    private List<Notas> obterNotas() throws IOException {
        Request postRequest = criarRequestParaNotas();

        try (Response response = client.newCall(postRequest).execute()) {
            if (!response.isSuccessful()) {
                sincronizado = false;
                logger.severe("Erro ao realizar o POST para obter notas: " + response);
                throw new IOException("Erro ao realizar o POST para obter notas: " + response);
            }
            return parseNotasFromHtml(response.body().string());
        }
    }


    /**
     * Cria a requisição HTTP para obter as notas.
     *
     * @return Requisição HTTP configurada.
     */
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

    /**
     * Parseia o HTML para extrair as notas.
     *
     * @param html Conteúdo HTML.
     * @return Lista de notas extraídas.
     */
    private List<Notas> parseNotasFromHtml(String html) {

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

    /**
     * Parseia um elemento HTML para extrair uma nota.
     *
     * @param linha Elemento HTML representando uma linha da tabela.
     * @return Objeto Notas extraído ou null em caso de erro.
     */
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

    /**
     * Obtém as notas existentes no Firestore para o usuário especificado.
     *
     * @param uid Identificador do usuário.
     * @return Lista de notas existentes no Firestore.
     * @throws InterruptedException Em caso de erro de execução.
     * @throws ExecutionException Em caso de erro de execução.
     */
    private ArrayList<Notas> obterNotasExistentesDoFirestore(String uid) throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> future = db.collection("usuarios")
                .document(uid)
                .collection("disciplinas")
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        ArrayList<Notas> notasExistentes = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Notas nota = document.toObject(Notas.class);
            notasExistentes.add(nota);
        }
        return notasExistentes;
    }


    /**
     * Coleta os dados do perfil do usuário.
     *
     * @param cpf CPF do usuário.
     * @return Objeto Perfil contendo os dados do usuário.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    private Perfil coletarDadosPerfil(String cpf,String email) throws IOException {
        Request getRequest = new Request.Builder()
                .url("https://sig.ifrs.edu.br/sigaa/portais/discente/discente.jsf")
                .get()
                .build();

        try (Response response = client.newCall(getRequest).execute()) {
            if (!response.isSuccessful()) {
                logger.severe("Erro ao carregar a página de perfil: " + response);
                throw new IOException("Erro ao carregar a página de perfil: " + response);
            }
            return parsePerfilFromHtml(response.body().string(), cpf, email);
        }
    }

/**
 * Parseia o HTML para extrair os dados do perfil.
 *
 * @param html Conteúdo HTML.
 * @param cpf  CPF do usuário.
 * @return Objeto Perfil extraído.
 * @throws IOException Se ocorrer um erro de I/O.
 */
    private Perfil parsePerfilFromHtml(String html, String cpf, String email) throws IOException {
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
        //String email = firestoreUtils.getEmailFromFirestore(cpf);
        String integralizado = calculaIntegralizacao(chObrigatoriaPendente, chOptativaPendente, chTotalCurriculo, chComplementarPendente);



        return new Perfil(
                nomeDocente,
                email,
                matricula,
                cpf,
                curso,
                nivel,
                status,
                anoIngresso,
                fotoBase64,
                chObrigatoriaPendente,
                chOptativaPendente,
                chTotalCurriculo,
                chComplementarPendente,
                integralizado,
                new ArrayList<>()
        );
    }


    /**
     * Atualiza os dados do perfil no Firestore.
     *
     * @param uid    Identificador do usuário.
     * @param perfil Objeto Perfil contendo os dados a serem atualizados.
     */
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

    /**
     * Calcula a integralização do curso.
     *
     * @param chObrigatoriaPendente CH obrigatória pendente.
     * @param chOptativaPendente    CH optativa pendente.
     * @param chTotalCurriculo      CH total do currículo.
     * @param chComplementarPendente CH complementar pendente.
     * @return Percentual de integralização.
     */
    private String calculaIntegralizacao(String chObrigatoriaPendente, String chOptativaPendente, String chTotalCurriculo, String chComplementarPendente) {
        return String.format("%.0f", 100 - ((Float.parseFloat(chObrigatoriaPendente) + Float.parseFloat(chOptativaPendente) + Float.parseFloat(chComplementarPendente)) * 100 / Float.parseFloat(chTotalCurriculo)));
    }

    /**
     * Baixa uma imagem e a converte para Base64.
     *
     * @param imgUrl URL da imagem.
     * @return String da imagem em Base64.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    private String baixarImagemEConverterParaBase64(String imgUrl) throws IOException {
        if (!imgUrl.startsWith("http")) {
            imgUrl = "https://sig.ifrs.edu.br" + imgUrl;  // Adiciona o domínio base para URLs relativos
        }
        Request request = new Request.Builder()
                .url(imgUrl)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.severe("Erro ao baixar a imagem: " + response);
                throw new IOException("Erro ao baixar a imagem: " + response);
            }

            byte[] imageBytes = response.body().bytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }


    /**
     * Limpa os cookies armazenados.
     */
    private void limparCookies() {
        cookieStore.clear();
        logger.info("Cookies limpos após o processo.");
    }
}


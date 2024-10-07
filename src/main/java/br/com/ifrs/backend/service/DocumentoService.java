/**
 * Serviço responsável por manipular documentos.
 */
package br.com.ifrs.backend.service;

import br.com.ifrs.backend.exception.UnauthorizedException;
import br.com.ifrs.backend.exception.VinculoBusinessException;
import br.com.ifrs.backend.utils.FirestoreUtils;
import jakarta.enterprise.context.ApplicationScoped;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Classe que fornece serviços relacionados a documentos.
 */
@ApplicationScoped
public class DocumentoService {

    private static final Logger logger = Logger.getLogger(DocumentoService.class.getName());
    private final FirestoreUtils firestoreUtils = new FirestoreUtils();
    private final java.util.Map<String, List<Cookie>> cookieStore = new java.util.HashMap<>();
    private final OkHttpClient client;
    private static final String SIGAA_URL = "https://sig.ifrs.edu.br/sigaa/logar.do?dispatch=logOn";

    /**
     * Construtor que inicializa o cliente HTTP com configurações de timeout e gerenciamento de cookies.
     */
    public DocumentoService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // Definir timeout de conexão para 30 segundos
                .readTimeout(30, TimeUnit.SECONDS)     // Definir timeout de leitura para 30 segundos
                .writeTimeout(30, TimeUnit.SECONDS)
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

    /**
     * Faz o download de um documento PDF e o retorna como uma string codificada em Base64.
     *
     * @param uid   Identificador do usuário.
     * @param tipo  Tipo do documento.
     * @param senha Senha do usuário.
     * @return String codificada em Base64 do PDF.
     * @throws IOException Se ocorrer um erro durante o download.
     */
    public String downloadPdfAsBase64(String uid, String tipo, String senha) throws IOException {

        if (uid == null || tipo == null || senha == null) {
            throw new IllegalArgumentException("Argumentos nulos para downloadPdfAsBase64");
        }
        try {
            String cpf = firestoreUtils.getCpfFromFirestore(uid);
            // Realizar login no SIGAA
            if (!performLogin(cpf, senha)) {
                throw new UnauthorizedException("Falha ao realizar login no SIGAA");
            }

            // Baixar o documento com base no tipo (historico, ementas, etc.)
            return baixarDocumento(uid, tipo);
        } finally {
            // Garantir que os cookies sejam limpos após o processo
            limparCookies();
        }
    }

    /**
     * Realiza o login no sistema SIGAA.
     *
     * @param username Nome de usuário.
     * @param password Senha do usuário.
     * @return true se o login for bem-sucedido, false caso contrário.
     * @throws IOException Se ocorrer um erro durante o login.
     */
    public boolean performLogin(String username, String password) throws IOException {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Usuario ou senha nulos");
        }
        FormBody formBody = new FormBody.Builder()
                .add("user.login", username)
                .add("user.senha", password)
                .build();

        Request postLoginRequest = new Request.Builder()
                .url(SIGAA_URL)
                .post(formBody)
                .build();

        try (Response response = client.newCall(postLoginRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro ao realizar login: " + response);
            }
            String responseBody = response.body().string();
            return !responseBody.contains("Usuário e/ou senha inválidos");
        }
    }

    /**
     * Baixa o documento do tipo especificado para o usuário.
     *
     * @param uid  Identificador do usuário.
     * @param tipo Tipo do documento.
     * @return String codificada em Base64 do PDF.
     * @throws IOException Se ocorrer um erro durante o download.
     */
    protected String baixarDocumento(String uid, String tipo) throws IOException {
        if (uid == null || tipo == null) {
            throw new IllegalArgumentException("Argumentos nulos para baixarDocumento");
        }

        logger.info("Baixando documento: " + tipo + " para o usuário: " + uid);
        String postUrl = "https://sig.ifrs.edu.br/sigaa/portais/discente/discente.jsf";
        FormBody formBody = new FormBody.Builder()
                .add("menu:form_menu_discente", "menu:form_menu_discente")
                .add("id", "11278")
                .add("jscook_action", getJscookAction(tipo))
                .add("javax.faces.ViewState", "j_id1")
                .build();

        Request postRequest = new Request.Builder()
                .url(postUrl)
                .post(formBody)
                .build();

        try (Response postResponse = client.newCall(postRequest).execute()) {
            if (!postResponse.isSuccessful()) {
                throw new IOException("Erro ao realizar o POST: " + postResponse);
            }

            // Para o tipo "atestadoMatricula", converte o HTML para PDF e retorna como Base64
            if (tipo.equals("atestadoMatricula")) {
                Document documentAtestadoMatricula = Jsoup.parse(postResponse.body().string());  // Converte o HTML para um documento Jsoup

                Element statusElement = documentAtestadoMatricula.selectFirst("td:contains(Status:) + td");

                if (statusElement != null && statusElement.text().contains("TRANCADO")) {
                    logger.info("Usuário não possui vínculo ativo "+uid);
                    throw new VinculoBusinessException("Usuário não possui vínculo ativo");
                }


                documentAtestadoMatricula.outputSettings().syntax(Document.OutputSettings.Syntax.xml); // Garante que o output seja XHTML
                String xhtml = documentAtestadoMatricula.html();  // Obtém o HTML corrigido

                // Renderiza o XHTML em PDF usando Flying Saucer
                try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                    ITextRenderer renderer = new ITextRenderer();
                    renderer.setDocumentFromString(xhtml);  // Usa o HTML corrigido
                    renderer.layout();
                    renderer.createPDF(byteArrayOutputStream); // Gera o PDF no ByteArrayOutputStream
                    byteArrayOutputStream.flush();

                    // Converte o PDF para Base64
                    return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IOException("Erro ao renderizar o PDF", e);
                }
            }

            // Caso não seja "atestadoMatricula", continuar com o fluxo padrão de download
            logger.info("Documento baixado com sucesso: \n" + postResponse.body());
            InputStream inputStream = postResponse.body().byteStream();
            String base64Pdf = Base64.getEncoder().encodeToString(inputStream.readAllBytes());
            return base64Pdf;
        }
    }


    /**
     * Retorna a ação do JSCook para o tipo de documento especificado.
     *
     * @param tipo Tipo do documento.
     * @return Ação do JSCook.
     * @throws IllegalArgumentException Se o tipo de documento for inválido.
     */
    private String getJscookAction(String tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de documento nulo para getJscookAction");
        }

        return switch (tipo) {
            case "historico" -> "menu_form_menu_discente_j_id_jsp_925609363_97_menu:A]#{ portalDiscente.historico }";
            case "historicoEmentas" -> "menu_form_menu_discente_j_id_jsp_925609363_97_menu:A]#{ portalDiscente.historicoComEmentas }";
            case "declaracaoVinculo" -> "menu_form_menu_discente_j_id_jsp_925609363_97_menu:A]#{ declaracaoVinculo.emitirDeclaracao }";
            case "atestadoMatricula" -> "menu_form_menu_discente_j_id_jsp_925609363_97_menu:A]#{ portalDiscente.atestadoMatricula }";
            default -> throw new IllegalArgumentException("Tipo de documento inválido");
        };
    }

//    private void salvarPdf(InputStream inputStream, String filePath) throws IOException {
//        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
//            byte[] buffer = new byte[2048];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                fileOutputStream.write(buffer, 0, bytesRead);
//            }
//            logger.info("PDF baixado e salvo como: " + filePath);
//        } catch (IOException e) {
//            throw new IOException("Erro ao salvar o arquivo PDF", e);
//        }
//    }

    /**
     * Limpa os cookies após o processo.
     */
    private void limparCookies() {

        cookieStore.clear();
        logger.info("Cookies limpos após o processo.");
    }

}




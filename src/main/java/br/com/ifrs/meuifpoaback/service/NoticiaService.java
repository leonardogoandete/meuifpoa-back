
package br.com.ifrs.meuifpoaback.service;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import br.com.ifrs.meuifpoaback.model.Noticia;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço responsável por obter notícias do site do IFRS.
 */
@Slf4j
@ApplicationScoped
public class NoticiaService {
    /**
     * Obtém uma lista de notícias do site do IFRS.
     *
     * @param limit        Limite de notícias a serem obtidas.
     * @param filterSearch Filtro de busca para as notícias.
     * @return Lista de notícias.
     */
    public List<Noticia> obterNoticias(int limit, String filterSearch) {
        List<Noticia> noticias = new ArrayList<>();
        String url = "https://poa.ifrs.edu.br/index.php/ultimas-noticias/noticias-principais";
        String postData = configuraFiltroNoticia(limit, filterSearch); // Se houver dados a serem enviados no corpo da solicitação POST
        String contentType = "application/x-www-form-urlencoded";

        try {
            // Realiza a solicitação POST para obter o conteúdo da página desejada
            HttpResponse<String> response = configuraConexao(url, contentType, postData);
            // Realiza o scraping das informações HTML da página
            scrapingInformacoesHtml(response, noticias);

        } catch (IOException | InterruptedException | URISyntaxException e) {
            System.out.println("Erro ao obter as notícias." + e.getMessage());
        }
        return noticias;
    }

    /**
     * Configura a conexão HTTP para realizar a solicitação POST.
     *
     * @param url         URL da solicitação.
     * @param contentType Tipo de conteúdo da solicitação.
     * @param postData    Dados a serem enviados no corpo da solicitação POST.
     * @return Resposta HTTP da solicitação.
     * @throws URISyntaxException       Se a URL for inválida.
     * @throws IOException              Se ocorrer um erro de I/O.
     * @throws InterruptedException     Se a solicitação for interrompida.
     */
    private static HttpResponse<String> configuraConexao(String url, String contentType, String postData) throws URISyntaxException, IOException, InterruptedException {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL não pode ser nula ou vazia.");
        }
        if (contentType == null || contentType.isEmpty()) {
            throw new IllegalArgumentException("Content-Type não pode ser nulo ou vazio.");
        }
        if (postData == null) {
            postData = "";
        }

        HttpResponse<String> response;
        HttpClient httpClient = criaHttpClientIgnorandoSSL(); // Usa um cliente HTTP que ignora a verificação de SSL

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Content-Type", contentType)
                    .POST(HttpRequest.BodyPublishers.ofString(postData))
                    .build();

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao realizar a solicitação HTTP." + e.getMessage());
        }

        return response;
    }

    /**
     * Cria um cliente HTTP que ignora a verificação de SSL.
     *
     * @return Cliente HTTP configurado para ignorar SSL.
     */
    private static HttpClient criaHttpClientIgnorandoSSL() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            return HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar o HttpClient ignorando SSL.", e);
        }
    }

    /**
     * Realiza o scraping das informações HTML da resposta HTTP.
     *
     * @param response Resposta HTTP contendo o HTML.
     * @param noticias Lista de notícias a ser preenchida com os dados extraídos.
     */
    private static void scrapingInformacoesHtml(HttpResponse<String> response, List<Noticia> noticias) {
        if (response == null) {
            throw new IllegalArgumentException("A resposta não pode ser nula.");
        }
        if (noticias == null) {
            throw new IllegalArgumentException("A lista de notícias não pode ser nula.");
        }

        // Carrega o conteúdo HTML da página
        String htmlContent = response.body();

        // Parseia o HTML usando Jsoup
        Document document = Jsoup.parse(htmlContent);

        // Encontra todas as notícias na página
        Elements listaNoticias = document.select("div.tileItem");
        for (Element noticia : listaNoticias) {
            // Extrai o título e o resumo da notícia
            String titulo = noticia.select("h2.tileHeadline > a").text().trim();
            String linkNoticia = noticia.select("h2.tileHeadline > a").attr("href");
            String resumo = noticia.select("span.description > p").text().trim();

            // Extrai a data e a hora da publicação
            String dataPublicacao = noticia.select("div.span2.tileInfo > ul > li:nth-child(3)").text().trim();
            String horaPublicacao = noticia.select("div.span2.tileInfo > ul > li:nth-child(4)").text().trim();

            noticias.add(new Noticia(linkNoticia, titulo, resumo, dataPublicacao, horaPublicacao));
        }
    }

    /**
     * Configura o filtro de notícias para a solicitação POST.
     *
     * @param limit        Limite de notícias a serem obtidas.
     * @param filterSearch Filtro de busca para as notícias.
     * @return String de consulta configurada.
     */
    private String configuraFiltroNoticia(int limit, String filterSearch) {
        String queryString;
        return queryString = (filterSearch == null)
                ? ("filter-search=&limit=\"\"" + limit)
                : ("filter-search=" + filterSearch + "&limit=" + limit);
    }
}

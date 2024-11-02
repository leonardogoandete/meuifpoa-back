
package br.com.ifrs.meuifpoaback.service;

import br.com.ifrs.meuifpoaback.model.Edital;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
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
import java.util.logging.Logger;

/**
 * Serviço responsável por obter editais do site do IFRS.
 */
@Slf4j
@ApplicationScoped
public class EditalService {

    private static final Logger logger = Logger.getLogger(EditalService.class.getName());
    /**
     * Obtém uma lista de editais do site do IFRS.
     *
     * @param limit        Limite de editais a serem obtidas.
     * @param filterSearch Filtro de busca para as editais.
     * @return Lista de editais.
     */
    public List<Edital> obterEditais(int limit, String filterSearch) {
        List<Edital> editais = new ArrayList<>();
        String url = "https://poa.ifrs.edu.br/index.php/editais-marcadores";
        String postData = configuraFiltroEdital(limit, filterSearch); // Se houver dados a serem enviados no corpo da solicitação POST
        String contentType = "application/x-www-form-urlencoded";

        try {
            // Realiza a solicitação POST para obter o conteúdo da página desejada
            HttpResponse<String> response = configuraConexao(url, contentType, postData);
            // Realiza o scraping das informações HTML da página
            scrapingInformacoesHtml(response, editais);

        } catch (IOException | InterruptedException | URISyntaxException e) {
            logger.severe("Erro ao obter as editais." + e.getMessage());
            System.out.println("Erro ao obter as editais." + e.getMessage());
        }
        return editais;
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
            logger.severe("Erro ao realizar a solicitação HTTP." + e.getMessage());
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
            logger.severe("Erro ao criar o HttpClient ignorando SSL." + e.getMessage());
            throw new RuntimeException("Erro ao criar o HttpClient ignorando SSL.", e);
        }
    }

    /**
     * Realiza o scraping das informações HTML da resposta HTTP.
     *
     * @param response Resposta HTTP contendo o HTML.
     * @param editais Lista de notícias a ser preenchida com os dados extraídos.
     */
    private static void scrapingInformacoesHtml(HttpResponse<String> response, List<Edital> editais) {
        if (response == null) {
            throw new IllegalArgumentException("A resposta não pode ser nula.");
        }
        if (editais == null) {
            throw new IllegalArgumentException("A lista de editais não pode ser nula.");
        }

        // Carrega o conteúdo HTML da página
        String htmlContent = response.body();

        // Parseia o HTML usando Jsoup
        Document document = Jsoup.parse(htmlContent);

        // Seleciona as linhas da tabela de editais
        Elements listaEdital = document.select("tbody > tr");
        for (Element edital : listaEdital) {
            // Extrai o título do edital
            Element tituloElement = edital.selectFirst("td.list-title > a");
            String titulo = tituloElement != null ? tituloElement.text().trim() : "";

            // Extrai o link do edital
            String linkEdital = tituloElement != null ? tituloElement.attr("href").trim() : "";

            // Extrai a data de publicação do edital
            Element dataElement = edital.selectFirst("td.list-date");
            String dataPublicacao = dataElement != null ? dataElement.text().trim() : "";

            // Adiciona o edital à lista se os campos obrigatórios estiverem preenchidos
            if (!titulo.isEmpty() && !linkEdital.isEmpty() && !dataPublicacao.isEmpty()) {
                editais.add(new Edital(linkEdital, titulo, dataPublicacao));
            }
        }
    }


    /**
     * Configura o filtro de notícias para a solicitação POST.
     *
     * @param limit        Limite de notícias a serem obtidas.
     * @param filterSearch Filtro de busca para as notícias.
     * @return String de consulta configurada.
     */
    private String configuraFiltroEdital(int limit, String filterSearch) {
        String queryString;
        return queryString = (filterSearch == null)
                ? ("filter-search=&limit=\"\"" + limit)
                : ("filter-search=" + filterSearch + "&limit=" + limit);
    }
}

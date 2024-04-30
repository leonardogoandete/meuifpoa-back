package service;

import jakarta.enterprise.context.ApplicationScoped;
import model.Noticia;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class NoticiaService {
    public List<Noticia> obterNoticias(int limit, String filterSearch) {
        List<Noticia> noticias = new ArrayList<>();
        String url = "https://poa.ifrs.edu.br/index.php/ultimas-noticias/noticias-principais";
        String postData = configuraFiltroNoticia(limit,filterSearch); // Se houver dados a serem enviados no corpo da solicitação POST
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

    private static HttpResponse<String> configuraConexao(String url, String contentType, String postData) throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> response;
        try{
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Content-Type", contentType)
                    .POST(HttpRequest.BodyPublishers.ofString(postData))
                    .build();

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }catch (Exception e){
            response = null;
        }
        return response;
    }

    private static void scrapingInformacoesHtml(HttpResponse<String> response, List<Noticia> noticias) {
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

    private String configuraFiltroNoticia(int limit, String filterSearch) {
        if (filterSearch == null) {
            filterSearch = "";
        }
        return "filter-search="+filterSearch+"&limit="+limit;
    }
}

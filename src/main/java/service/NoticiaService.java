package service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.microsoft.playwright.*;
import jakarta.enterprise.context.ApplicationScoped;
import model.Noticia;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

//@ApplicationScoped
//public class NoticiaService {
//        public List<Noticia> obterNoticias() {
//            List<Noticia> noticias = new ArrayList<>();
//            try (Playwright playwright = Playwright.create()) {
//                // Inicia o navegador
//                Browser browser = playwright.firefox().launch();
//                // Abre uma nova página
//                Page page = browser.newPage();
//
//                // Realiza a solicitação POST para obter a página desejada
//                String url = "https://poa.ifrs.edu.br/index.php/ultimas-noticias/noticias-principais";
//                String postData = "limit=20"; // Se houver dados a serem enviados no corpo da solicitação POST, adicione aqui
//                String contentType = "application/x-www-form-urlencoded";
//
//                HttpRequest request = HttpRequest.newBuilder()
//                        .uri(new URI(url))
//                        .header("Content-Type", contentType)
//                        .POST(HttpRequest.BodyPublishers.ofString(postData))
//                        .build();
//
//                HttpClient httpClient = HttpClient.newHttpClient();
//                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//
//                // Carrega o conteúdo HTML da página
//                String htmlContent = response.body();
//                page.setContent(htmlContent);
//
//                // Realize a extração de dados da página como antes
//                List<ElementHandle> listaNoticias = page.querySelectorAll("div.tileItem");
//                for (ElementHandle noticia : listaNoticias) {
//                    // Extrai o título e o resumo da notícia
//                    String titulo = noticia.querySelector("h2.tileHeadline > a").innerText().trim();
//                    String linkNoticia = noticia.querySelector("h2.tileHeadline > a").getAttribute("href");
//                    String resumo = noticia.querySelector("span.description > p").innerText().trim();
//
//                    // Extrai a data e a hora da publicação
//                    String dataPublicacao = noticia.querySelector("div.span2.tileInfo > ul > li:nth-child(3)").innerText().trim();
//                    String horaPublicacao = noticia.querySelector("div.span2.tileInfo > ul > li:nth-child(4)").innerText().trim();
//
//                    noticias.add(
//                            new Noticia(linkNoticia,
//                                    titulo,
//                                    resumo,
//                                    dataPublicacao,
//                                    horaPublicacao));
//                }
//
//                browser.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return noticias;
//        }

@ApplicationScoped
public class NoticiaService {
    public List<Noticia> obterNoticias(int limit) {
        List<Noticia> noticias = new ArrayList<>();
        try {
            // Realiza a solicitação POST para obter o conteúdo da página desejada
            String url = "https://poa.ifrs.edu.br/index.php/ultimas-noticias/noticias-principais";
            String postData = "limit="+limit; // Se houver dados a serem enviados no corpo da solicitação POST, adicione aqui
            String contentType = "application/x-www-form-urlencoded";

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Content-Type", contentType)
                    .POST(HttpRequest.BodyPublishers.ofString(postData))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

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
        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
        }
        return noticias;
    }
}

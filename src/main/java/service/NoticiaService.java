package service;

import com.microsoft.playwright.*;
import jakarta.enterprise.context.ApplicationScoped;
import model.Noticia;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class NoticiaService {
        public List<Noticia> obterNoticias() {
            List<Noticia> noticias = new ArrayList<>();
            try (Playwright playwright = Playwright.create()) {
                // Inicia o navegador
                Browser browser = playwright.firefox().launch();
                // Abre uma nova página
                Page page = browser.newPage();

                // Realiza a solicitação POST para obter a página desejada
                String url = "https://poa.ifrs.edu.br/index.php/ultimas-noticias/noticias-principais";
                String postData = "limit=100"; // Se houver dados a serem enviados no corpo da solicitação POST, adicione aqui
                String contentType = "application/x-www-form-urlencoded";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(url))
                        .header("Content-Type", contentType)
                        .POST(HttpRequest.BodyPublishers.ofString(postData))
                        .build();

                HttpClient httpClient = HttpClient.newHttpClient();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // Carrega o conteúdo HTML da página
                String htmlContent = response.body();
                page.setContent(htmlContent);

                // Realize a extração de dados da página como antes
                List<ElementHandle> listaNoticias = page.querySelectorAll("div.tileItem");
                for (ElementHandle noticia : listaNoticias) {
                    // Extrai o título e o resumo da notícia
                    String titulo = noticia.querySelector("h2.tileHeadline > a").innerText().trim();
                    String linkNoticia = noticia.querySelector("h2.tileHeadline > a").getAttribute("href");
                    String resumo = noticia.querySelector("span.description > p").innerText().trim();

                    // Extrai a data e a hora da publicação
                    String dataPublicacao = noticia.querySelector("div.span2.tileInfo > ul > li:nth-child(3)").innerText().trim();
                    String horaPublicacao = noticia.querySelector("div.span2.tileInfo > ul > li:nth-child(4)").innerText().trim();

                    noticias.add(
                            new Noticia(linkNoticia,
                                    titulo,
                                    resumo,
                                    dataPublicacao,
                                    horaPublicacao));
                }

                browser.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return noticias;
        }
}
package br.com.ifrs.backend.service;

import br.com.ifrs.backend.model.Noticia;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@ApplicationScoped
public class SyncNoticias {
    private final Firestore db = FirestoreClient.getFirestore();
    private static final Logger logger = Logger.getLogger(SyncNoticias.class.getName());

    @Scheduled(cron = "{agendamento}")
    public void syncNoticias() {
        logger.info("Iniciando sincronização de notícias");

        // Obtenha as notícias
        // Adicionado o limite para nao exeder o uso no Firebase
        List<Noticia> novasNoticias = obterNoticias(10, ""); // Limite de 10 notícias sem filtro de busca

        // Salva as notícias no Firestore
        try {
            salvarNoticiasNoFirestore(novasNoticias);
        } catch (InterruptedException | ExecutionException e) {
            logger.severe("Erro ao salvar as notícias no Firestore: " + e.getMessage());
        }

        logger.info("Sincronização de notícias concluída");
    }

    public List<Noticia> obterNoticias(int limit, String filterSearch) {
        List<Noticia> noticias = new ArrayList<>();
        String url = "https://poa.ifrs.edu.br/index.php/ultimas-noticias/noticias-principais";
        String postData = configuraFiltroNoticia(limit, filterSearch);
        String contentType = "application/x-www-form-urlencoded";

        try {
            HttpResponse<String> response = configuraConexao(url, contentType, postData);
            scrapingInformacoesHtml(response, noticias);

        } catch (IOException | InterruptedException | URISyntaxException e) {
            logger.severe("Erro ao obter as notícias: " + e.getMessage());
        }
        return noticias;
    }

    private static HttpResponse<String> configuraConexao(String url, String contentType, String postData) throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> response;
        HttpClient httpClient = criaHttpClientIgnorandoSSL();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Content-Type", contentType)
                    .POST(HttpRequest.BodyPublishers.ofString(postData))
                    .build();

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao realizar a solicitação HTTP: " + e.getMessage());
        }

        return response;
    }

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

    private static void scrapingInformacoesHtml(HttpResponse<String> response, List<Noticia> noticias) {
        String htmlContent = response.body();
        Document document = Jsoup.parse(htmlContent);
        Elements listaNoticias = document.select("div.tileItem");

        for (Element noticia : listaNoticias) {
            String titulo = noticia.select("h2.tileHeadline > a").text().trim();
            String linkNoticia = noticia.select("h2.tileHeadline > a").attr("href");
            String resumo = noticia.select("span.description > p").text().trim();
            String dataPublicacao = noticia.select("div.span2.tileInfo > ul > li:nth-child(3)").text().trim();
            String horaPublicacao = noticia.select("div.span2.tileInfo > ul > li:nth-child(4)").text().trim();

            noticias.add(new Noticia(linkNoticia, titulo, resumo, dataPublicacao, horaPublicacao));
        }
    }

    private String configuraFiltroNoticia(int limit, String filterSearch) {
        if (filterSearch == null) {
            filterSearch = "";
        }
        return "filter-search=" + filterSearch + "&limit=" + limit;
    }

    private void salvarNoticiasNoFirestore(List<Noticia> novasNoticias) throws InterruptedException, ExecutionException {
        CollectionReference noticiasCollection = db.collection("noticias");

        for (Noticia novaNoticia : novasNoticias) {
            String docId = gerarIdDocumento(novaNoticia.getLink());
            DocumentReference docRef = noticiasCollection.document(docId);
            try {
                ApiFuture<WriteResult> result = docRef.set(novaNoticia);
                result.get();
            } catch (Exception e) {
                logger.severe("Erro ao salvar notícia no Firestore: " + novaNoticia.getTitulo() + " - " + e.getMessage());
            }
        }
    }

    private String gerarIdDocumento(String link) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(link.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash MD5 para o link: " + link, e);
        }
    }

}

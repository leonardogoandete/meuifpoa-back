package br.com.ifrs.backend;

import okhttp3.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OkHttpLoginExample {

    private final OkHttpClient client;

    public OkHttpLoginExample() {
        // Usar CookieJar para armazenar e enviar cookies automaticamente
        this.client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    private final java.util.Map<String, List<Cookie>> cookieStore = new java.util.HashMap<>();

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

    public String login(String loginUrl, String username, String password) throws IOException {
        // Passo 1: Obter a página de login e capturar cookies de sessão
        Request getLoginPageRequest = new Request.Builder()
                .url(loginUrl)
                .get()
                .build();

        // Executar a requisição para carregar a página de login
        Response response = client.newCall(getLoginPageRequest).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Erro ao carregar a página de login: " + response);
        }

        response.close();

        // Passo 2: Enviar o formulário de login com os cookies capturados automaticamente
        FormBody formBody = new FormBody.Builder()
                .add("user.login", username)
                .add("user.senha", password)
                .build();

        Request postLoginRequest = new Request.Builder()
                .url(loginUrl)
                .post(formBody)
                .build();

        try (Response loginResponse = client.newCall(postLoginRequest).execute()) {
            if (!loginResponse.isSuccessful()) {
                throw new IOException("Erro ao realizar o login: " + loginResponse);
            }

            // Retornar o corpo da resposta após o login
            return loginResponse.body().string();
        }
    }

    public String obterNotas() throws IOException {
        // URL para a segunda requisição (menu discente)
        String postUrl = "https://sig.ifrs.edu.br/sigaa/portais/discente/discente.jsf";

        // Construir os dados do formulário para o POST
        FormBody formBody = new FormBody.Builder()
                .add("menu:form_menu_discente", "menu:form_menu_discente")
                .add("id", "11278")
                .add("jscook_action", "menu_form_menu_discente_j_id_jsp_925609363_97_menu:A]#{ relatorioNotasAluno.gerarRelatorio }")
                .add("javax.faces.ViewState", "j_id1") // Ajustar para capturar dinamicamente se necessário
                .build();

        // Enviar a requisição POST com os cookies da sessão
        Request postRequest = new Request.Builder()
                .url(postUrl)
                .post(formBody)
                .build();

        try (Response postResponse = client.newCall(postRequest).execute()) {
            if (!postResponse.isSuccessful()) {
                throw new IOException("Erro ao realizar o POST: " + postResponse);
            }

            // Retornar o corpo da resposta do POST
            return postResponse.body().string();
        }
    }

    public void obterHistorico() throws IOException {
        // URL para a segunda requisição (menu discente)
        String postUrl = "https://sig.ifrs.edu.br/sigaa/portais/discente/discente.jsf";

        // Construir os dados do formulário para o POST
        FormBody formBody = new FormBody.Builder()
                .add("menu:form_menu_discente", "menu:form_menu_discente")
                .add("id", "11278")
                .add("jscook_action", "menu_form_menu_discente_j_id_jsp_925609363_97_menu:A]#{ portalDiscente.historico }")
                .add("javax.faces.ViewState", "j_id1") // Ajustar para capturar dinamicamente se necessário
                .build();

        // Enviar a requisição POST com os cookies da sessão
        Request postRequest = new Request.Builder()
                .url(postUrl)
                .post(formBody)
                .build();

        try (Response postResponse = client.newCall(postRequest).execute()) {
            if (!postResponse.isSuccessful()) {
                throw new IOException("Erro ao realizar o POST: " + postResponse);
            }

            // Receber o corpo da resposta como um InputStream
            InputStream inputStream = postResponse.body().byteStream();

            // Defina o caminho do arquivo onde o PDF será salvo
            String filePath = "historico.pdf";

            // Salvar o arquivo PDF no disco
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                byte[] buffer = new byte[2048];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("PDF baixado e salvo como: " + filePath);
            } catch (IOException e) {
                throw new IOException("Erro ao salvar o arquivo PDF", e);
            }
        }
    }

    public void obterHistoricoEmentas() throws IOException {
        // URL para a segunda requisição (menu discente)
        String postUrl = "https://sig.ifrs.edu.br/sigaa/portais/discente/discente.jsf";

        // Construir os dados do formulário para o POST
        FormBody formBody = new FormBody.Builder()
                .add("menu:form_menu_discente", "menu:form_menu_discente")
                .add("id", "11278")
                .add("jscook_action", "menu_form_menu_discente_j_id_jsp_925609363_97_menu:A]#{ portalDiscente.historicoComEmentas }")
                .add("javax.faces.ViewState", "j_id1") // Ajustar para capturar dinamicamente se necessário
                .build();

        // Enviar a requisição POST com os cookies da sessão
        Request postRequest = new Request.Builder()
                .url(postUrl)
                .post(formBody)
                .build();

        try (Response postResponse = client.newCall(postRequest).execute()) {
            if (!postResponse.isSuccessful()) {
                throw new IOException("Erro ao realizar o POST: " + postResponse);
            }

            // Receber o corpo da resposta como um InputStream
            InputStream inputStream = postResponse.body().byteStream();

            // Defina o caminho do arquivo onde o PDF será salvo
            String filePath = "historico_ementas.pdf";

            // Salvar o arquivo PDF no disco
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                byte[] buffer = new byte[2048];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("PDF baixado e salvo como: " + filePath);
            } catch (IOException e) {
                throw new IOException("Erro ao salvar o arquivo PDF", e);
            }
        }
    }

    public void obterDeclaracaoVinculo() throws IOException {
        // URL para a segunda requisição (menu discente)
        String postUrl = "https://sig.ifrs.edu.br/sigaa/portais/discente/discente.jsf";

        // Construir os dados do formulário para o POST
        FormBody formBody = new FormBody.Builder()
                .add("menu:form_menu_discente", "menu:form_menu_discente")
                .add("id", "11278")
                .add("jscook_action", "menu_form_menu_discente_j_id_jsp_925609363_97_menu:A]#{ declaracaoVinculo.emitirDeclaracao }")
                .add("javax.faces.ViewState", "j_id1") // Ajustar para capturar dinamicamente se necessário
                .build();

        // Enviar a requisição POST com os cookies da sessão
        Request postRequest = new Request.Builder()
                .url(postUrl)
                .post(formBody)
                .build();

        try (Response postResponse = client.newCall(postRequest).execute()) {
            if (!postResponse.isSuccessful()) {
                throw new IOException("Erro ao realizar o POST: " + postResponse);
            }

            // Receber o corpo da resposta como um InputStream
            InputStream inputStream = postResponse.body().byteStream();

            // Defina o caminho do arquivo onde o PDF será salvo
            String filePath = "declaracao_vinculo.pdf";

            // Salvar o arquivo PDF no disco
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                byte[] buffer = new byte[2048];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("PDF baixado e salvo como: " + filePath);
            } catch (IOException e) {
                throw new IOException("Erro ao salvar o arquivo PDF", e);
            }
        }
    }

    public void obterAtestadoMatricula() throws IOException {
        // URL para a segunda requisição (menu discente)
        String postUrl = "https://sig.ifrs.edu.br/sigaa/portais/discente/discente.jsf";

        // Construir os dados do formulário para o POST
        FormBody formBody = new FormBody.Builder()
                .add("menu:form_menu_discente", "menu:form_menu_discente")
                .add("id", "11278")
                .add("jscook_action", "menu_form_menu_discente_j_id_jsp_925609363_97_menu:A]#{ portalDiscente.atestadoMatricula }")
                .add("javax.faces.ViewState", "j_id1") // Ajustar para capturar dinamicamente se necessário
                .build();

        // Enviar a requisição POST com os cookies da sessão
        Request postRequest = new Request.Builder()
                .url(postUrl)
                .post(formBody)
                .build();

        try (Response postResponse = client.newCall(postRequest).execute()) {
            if (!postResponse.isSuccessful()) {
                throw new IOException("Erro ao realizar o POST: " + postResponse);
            }

            //imprimir a pagina como pdf
            System.out.println(postResponse.body().string());

        }
    }

    public static void main(String[] args) {
        OkHttpLoginExample loginExample = new OkHttpLoginExample();
        try {
            // URL da página de login
            String loginUrl = "https://sig.ifrs.edu.br/sigaa/logar.do?dispatch=logOn";
            String username = "";
            String password = "";

            // Realiza o login
            loginExample.login(loginUrl, username, password);

            // Obter notas
            String notas = loginExample.obterNotas();
            System.out.println(notas);

            // Obter histórico
            //loginExample.obterHistorico();

            // Obter histórico com ementas
            //loginExample.obterHistoricoEmentas();

            // Obter declaração de vínculo
            //loginExample.obterDeclaracaoVinculo();

            // Obter atestado de matrícula
            //loginExample.obterAtestadoMatricula();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package br.com.ifrs.meuifpoaback;

import br.com.ifrs.meuifpoaback.client.SigaaClient;
import br.com.ifrs.meuifpoaback.model.AlunoSigaa;
import br.com.ifrs.meuifpoaback.model.ObterToken;
import br.com.ifrs.meuifpoaback.model.RespostaToken;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.net.URI;
import java.util.Map;

public class Teste {
    public static void main(String[] args) {
        // Criando o cliente REST manualmente para a base URI principal
        SigaaClient sigaaClient = RestClientBuilder.newBuilder()
                .baseUri(URI.create("https://dev8e.ifrs.edu.br"))
                .build(SigaaClient.class);

        // Criando a requisição de token
        ObterToken request = new ObterToken("client_credentials", "xxxxxx", "xxxxx");

        // Chamando a API para obter o token
        RespostaToken response = sigaaClient.getToken(request);

        // Verificando se o token foi obtido com sucesso
        if (response != null && response.getAccessToken() != null) {
            String accessToken = response.getAccessToken();
            String authorizationHeader = "Bearer " + accessToken;
            System.out.println("Token obtido: " + accessToken);
            System.out.println("Authorization header: " + authorizationHeader);

            // Realizando a chamada para buscar o aluno
            //Map<String, AlunoSigaa> alunoMap = sigaaClient.getAluno("2020007666", authorizationHeader);
            //AlunoSigaa aluno = alunoMap.values().iterator().next();
            //System.out.println("Dados do aluno: " + aluno.getLogin());
        } else {
            System.out.println("Falha ao obter o token.");
        }
    }
}



//ObterToken request = new ObterToken("client_credentials", "9d26b45b-2270-42bb-ba49-574442bcf1db", "bY9VpXbtMb0jLSZWxGVkOZEZiLJj1otXsvrCxocG");
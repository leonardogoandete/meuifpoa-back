package br.com.ifrs.backend.controller;

import br.com.ifrs.backend.model.Noticia;
import br.com.ifrs.backend.service.NoticiaService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

@QuarkusTest
public class NoticiaControllerTest {

    @InjectMock
    NoticiaService noticiaService;  // Mocking the NoticiaService

    @Test
    public void testObterNoticiasSuccess() {
        // Criar uma lista de notícias fictícias para o mock
        List<Noticia> noticiasMock = new ArrayList<>();
        noticiasMock.add(new Noticia("link1", "Notícia 1", "Resumo 1", "01/10/2024", "12:00"));
        noticiasMock.add(new Noticia("link2", "Notícia 2", "Resumo 2", "02/10/2024", "13:00"));

        // Mockar o serviço para retornar a lista de notícias
        when(noticiaService.obterNoticias(10, "IFRS")).thenReturn(noticiasMock);

        // Executar o teste
        given()
                .queryParam("limit", 10)
                .queryParam("filter", "IFRS")
                .when()
                .post("/noticias")
                .then()
                .statusCode(200)  // Verificar se o status retornado é 200 (OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body("", hasSize(2))  // Verificar se o corpo da resposta tem 2 notícias
                .body("[0].titulo", equalTo("Notícia 1"))
                .body("[0].resumo", equalTo("Resumo 1"))
                .body("[1].titulo", equalTo("Notícia 2"))
                .body("[1].resumo", equalTo("Resumo 2"));

        // Verificar se o serviço foi chamado com os parâmetros corretos
        verify(noticiaService, times(1)).obterNoticias(10, "IFRS");
    }

    @Test
    public void testObterNoticiasError() {
        // Mockar o serviço para lançar uma exceção
        when(noticiaService.obterNoticias(anyInt(), anyString())).thenThrow(new RuntimeException("Erro ao obter notícias"));

        // Executar o teste
        given()
                .queryParam("limit", 10)
                .queryParam("filter", "IFRS")
                .when()
                .post("/noticias")
                .then()
                .statusCode(500)  // Verificar se o status retornado é 500 (Internal Server Error)
                .body(equalTo("Erro ao consultar notícias!"));  // Verificar se a mensagem de erro é a correta

        // Verificar se o serviço foi chamado com os parâmetros corretos
        verify(noticiaService, times(1)).obterNoticias(10, "IFRS");
    }
}

package br.com.ifrs.backend.controller;

import br.com.ifrs.backend.model.DocumentoRequest;
import br.com.ifrs.backend.service.DocumentoService;
import br.com.ifrs.backend.exception.UnauthorizedException;
import br.com.ifrs.backend.exception.VinculoBusinessException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import jakarta.inject.Inject;  // Use 'jakarta.inject.Inject' aqui

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@QuarkusTest
public class DocumentoControllerTest {

    @InjectMock
    DocumentoService documentoService;  // Use '@InjectMock' fornecido pelo Quarkus

    @Test
    @TestSecurity(user = "testUser", roles = {"USER"})
    public void testGetDocumentoEndpointSuccess() throws Exception {
        // Mocking the DocumentoService response
        when(documentoService.downloadPdfAsBase64("testUser", "historico", "senha123"))
                .thenReturn("mocked_base64_pdf");

        // Prepare request object
        DocumentoRequest documentoRequest = new DocumentoRequest("historico", "senha123");

        // Execute the test
        given()
                .contentType(ContentType.JSON)
                .body(documentoRequest)
                .when()
                .post("/documento")
                .then()
                .statusCode(200) // Expecting status 201 (CREATED)
                .body("pdfbase64", equalTo("mocked_base64_pdf"));
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"USER"})
    public void testGetDocumentoEndpointUnauthorized() throws Exception {
        // Mocking an UnauthorizedException
        when(documentoService.downloadPdfAsBase64("testUser", "historico", "senha123"))
                .thenThrow(new UnauthorizedException("Falha ao realizar login no SIGAA"));

        // Prepare request object
        DocumentoRequest documentoRequest = new DocumentoRequest("historico", "senha123");

        // Execute the test
        given()
                .contentType(ContentType.JSON)
                .body(documentoRequest)
                .when()
                .post("/documento")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"USER"})
    public void testGetDocumentoEndpointVinculoTrancado() throws Exception {
        // Mocking a VinculoBusinessException
        when(documentoService.downloadPdfAsBase64("testUser", "atestadoMatricula", "senha123"))
                .thenThrow(new VinculoBusinessException("Usuário não possui vínculo ativo"));

        // Prepare request object
        DocumentoRequest documentoRequest = new DocumentoRequest("atestadoMatricula", "senha123");

        // Execute the test
        given()
                .contentType(ContentType.JSON)
                .body(documentoRequest)
                .when()
                .post("/documento")
                .then()
                .statusCode(406) // Expecting status 406 (NOT_ACCEPTABLE)
                ;
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"USER"})
    public void testGetDocumentoEndpointInternalError() throws Exception {
        // Mocking a general exception
        when(documentoService.downloadPdfAsBase64("testUser", "historico", "senha123"))
                .thenThrow(new RuntimeException("Erro interno"));

        // Prepare request object
        DocumentoRequest documentoRequest = new DocumentoRequest("historico", "senha123");

        // Execute the test
        given()
                .contentType(ContentType.JSON)
                .body(documentoRequest)
                .when()
                .post("/documento")
                .then()
                .statusCode(500) // Expecting status 500 (INTERNAL SERVER ERROR)
                .body("mensagem", equalTo("Erro interno do servidor"));
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"USER"})
    public void testGetDocumentoComTipoNulo() {
        DocumentoRequest documentoRequest = new DocumentoRequest(null, "senha123");

        given()
                .contentType(ContentType.JSON)
                .body(documentoRequest)
                .when()
                .post("/documento")
                .then()
                .statusCode(400)  // Esperando um BAD_REQUEST
                .body("mensagem", equalTo("Argumentos nulos"));
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"USER"})
    public void testGetDocumentoComSenhaNula() {
        DocumentoRequest documentoRequest = new DocumentoRequest("teste", null);

        given()
                .contentType(ContentType.JSON)
                .body(documentoRequest)
                .when()
                .post("/documento")
                .then()
                .statusCode(400)  // Esperando um BAD_REQUEST
                .body("mensagem", equalTo("Argumentos nulos"));
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"USER"})
    public void testGetDocumentoComTipoESenhaNulos() {
        DocumentoRequest documentoRequest = new DocumentoRequest(null, null);

        given()
                .contentType(ContentType.JSON)
                .body(documentoRequest)
                .when()
                .post("/documento")
                .then()
                .statusCode(400);

    }
}

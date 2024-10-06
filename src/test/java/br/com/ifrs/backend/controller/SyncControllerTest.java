package br.com.ifrs.backend.controller;

import br.com.ifrs.backend.model.Login;
import br.com.ifrs.backend.service.SyncService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@QuarkusTest
public class SyncControllerTest {

    @InjectMock
    SyncService syncService;  // Mocking the SyncService

    @Test
    @TestSecurity(user = "testUser", roles = {"USER"})
    public void testSincronizarSuccess() throws Exception {
        // Mocking the SyncService to simulate success
        when(syncService.sincronizar("testUser", "senha123")).thenReturn(true);

        // Prepare login object
        Login login = new Login();
        login.setSenha("senha123");

        // Execute the test
        given()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/sync")
                .then()
                .statusCode(200)  // Expecting status 200 (OK)
                .body("mensagem", equalTo("Sincronização realizada com sucesso!"));

        // Verifying that the syncService.sincronizar method was called with the correct parameters
        verify(syncService, times(1)).sincronizar("testUser", "senha123");
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"USER"})
    public void testSincronizarInternalError() throws Exception {
        // Mocking the SyncService to throw a general exception
        doThrow(new RuntimeException("Erro inesperado")).when(syncService).sincronizar("testUser", "senha123");

        // Prepare login object
        Login login = new Login();
        login.setSenha("senha123");

        // Execute the test
        given()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/sync")
                .then()
                .statusCode(500)  // Expecting status 500 (INTERNAL SERVER ERROR)
                .body("mensagem", org.hamcrest.Matchers.equalTo("Erro interno do servidor"));

        // Verifying that the syncService.sincronizar method was called with the correct parameters
        verify(syncService, times(1)).sincronizar("testUser", "senha123");
    }


    @Test
    @TestSecurity(user = "testUser", roles = {"USER"})
    public void testSincronizarWithNullPassword() throws Exception {
        // Prepare login object with null password
        Login login = new Login();
        login.setSenha(null);

        // Execute the test
        given()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/sync")
                .then()
                .statusCode(400)  // Expecting status 400 (BAD REQUEST)
                .body("mensagem", equalTo("A senha não pode ser vazia."));

        // Verifying that the syncService.sincronizar method was not called because of the exception
        verify(syncService, times(0)).sincronizar(anyString(), anyString());
    }


    @Test
    @TestSecurity(user = "testUser", roles = {"USER"})
    public void testSincronizarUnauthorized() throws Exception {
        // Simular SecurityException no serviço
        when(syncService.sincronizar(anyString(), anyString()))
                .thenThrow(new SecurityException("Usuário não autorizado"));

        Login login = new Login("senha_teste");

        // Executar o teste
        given()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/sync")
                .then()
                .statusCode(401)
                .body("mensagem", equalTo("Usuário não autorizado"));
    }

}

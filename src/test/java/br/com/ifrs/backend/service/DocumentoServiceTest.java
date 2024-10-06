package br.com.ifrs.backend.service;

import br.com.ifrs.backend.exception.UnauthorizedException;
import br.com.ifrs.backend.exception.VinculoBusinessException;
import br.com.ifrs.backend.utils.FirestoreUtils;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentoServiceTest {

    private static String SIGAA_URL_MOCK = "https://sig.ifrs.edu.br/sigaa/logar.do?dispatch=logOn";

    @InjectMocks
    DocumentoService documentoService;

    @Mock
    FirestoreUtils firestoreUtils;

    private MockWebServer mockWebServer;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket("ifrspoa-d9f18.appspot.com")
                    .build();

            FirebaseApp.initializeApp(options);
        }

        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // Alterar a URL do SIGAA para apontar para o MockWebServer
        SIGAA_URL_MOCK = mockWebServer.url("/sigaa/logar.do?dispatch=logOn").toString();
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void testBaixarDocumentoSuccess() throws Exception {
        String uid = "testUser";
        String tipo = "historico";

        // Simular uma resposta de PDF baixado com sucesso
        String pdfContent = "mocked_pdf_content";
        mockWebServer.enqueue(new MockResponse().setBody(pdfContent).setResponseCode(200));

        // Executar o método
        String result = documentoService.baixarDocumento(uid, tipo);

        // Verificar o sucesso do download
        String expectedBase64 = Base64.getEncoder().encodeToString(pdfContent.getBytes());
        assertEquals(expectedBase64, result, "O conteúdo do PDF codificado em Base64 não corresponde ao esperado.");
    }



    @Test
    public void testDownloadPdfAsBase64Unauthorized() throws Exception {
        String uid = "testUser";
        String tipo = "historico";
        String senha = "senhaInvalida";

        // Simular uma resposta de falha de login no SIGAA
        mockWebServer.enqueue(new MockResponse().setBody("Usuário e/ou senha inválidos").setResponseCode(200));

        // Verificar se a UnauthorizedException é lançada
        assertThrows(UnauthorizedException.class, () -> {
            documentoService.downloadPdfAsBase64(uid, tipo, senha);
        }, "Deveria ter lançado UnauthorizedException, mas não lançou.");
    }


    @Test
    public void testBaixarDocumentoVinculoTrancado() throws Exception {
        String uid = "testUser";
        String tipo = "atestadoMatricula";

        // Simular uma resposta com status TRANCADO
        String htmlResponse = "<td>Status:</td><td>TRANCADO</td>";
        mockWebServer.enqueue(new MockResponse().setBody(htmlResponse).setResponseCode(200));

        // Verificar se a exceção VinculoBusinessException é lançada
        assertThrows(VinculoBusinessException.class, () -> {
            documentoService.baixarDocumento(uid, tipo);
        }, "Deveria ter lançado VinculoBusinessException, mas não lançou.");
    }

    @Test
    public void testPerformLoginSuccess() throws Exception {
        String cpf = "12345678900";
        String senha = "senha123";

        // Mockando a resposta de login bem-sucedido
        mockWebServer.enqueue(new MockResponse().setBody("Login realizado com sucesso").setResponseCode(200));

        // Executar o método
        boolean result = documentoService.performLogin(cpf, senha);

        // Verificar o sucesso do login
        assertTrue(result, "O login deveria ter sido bem-sucedido, mas não foi.");
    }

    @Test
    public void testPerformLoginFailure() throws Exception {
        String cpf = "12345678900";
        String senha = "senha123";

        // Mockando a resposta de falha no login no MockWebServer
        mockWebServer.enqueue(new MockResponse().setBody("Usuário e/ou senha inválidos").setResponseCode(200));

        // Executar o método
        boolean result = documentoService.performLogin(cpf, senha);

        // Verificações
        assertFalse(result);
    }
}

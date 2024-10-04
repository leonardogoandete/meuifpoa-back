//package br.com.ifrs.backend.service;
//
//import br.com.ifrs.backend.exception.UnauthorizedException;
//import br.com.ifrs.backend.exception.VinculoBusinessException;
//import br.com.ifrs.backend.utils.FirestoreUtils;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import okhttp3.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.stubbing.Answer;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class DocumentoServiceTest {
//
//    @InjectMocks
//    DocumentoService documentoService;
//
//    @Mock
//    FirestoreUtils firestoreUtils;
//
//    @Mock
//    OkHttpClient client;
//
//    @Mock
//    Call mockCall;
//
//    private String mockUid = "123";
//    private String mockCpf = "18035208764";
//    private String mockSenha = "senha123";
//
//    @BeforeEach
//    public void setup() throws IOException {
//        //COnfigurar firebase
//        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
//
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .setStorageBucket("ifrspoa-d9f18.appspot.com")
//                .build();
//
//        FirebaseApp.initializeApp(options);
//
//        // Mocking FirestoreUtils behavior
//        lenient().when(firestoreUtils.getCpfFromFirestore(mockUid)).thenReturn(mockCpf);
//    }
//
//    @Test
//    public void testDownloadPdfAsBase64Success() throws Exception {
//        // Mocking a successful response for the login
//        setupMockHttpClient(true, "<html><body>Mocked PDF Content</body></html>");
//
//        // Execute the method
//        String result = documentoService.downloadPdfAsBase64(mockUid, "historico", mockSenha);
//
//        // Asserting the base64 result
//        assertNotNull(result);
//    }
//
//    @Test
//    public void testDownloadPdfAsBase64VinculoTrancado() throws Exception {
//        // Mocking FirestoreUtils to return a CPF
//        when(firestoreUtils.getCpfFromFirestore(mockUid)).thenReturn(mockCpf);
//
//        // Mocking a response with "TRANCADO" status
//        String mockHtml = "<html><body><td>Status:</td><td>TRANCADO</td></body></html>";
//        setupMockHttpClient(true, mockHtml);
//
//        // Expects VinculoBusinessException
//        VinculoBusinessException exception = assertThrows(VinculoBusinessException.class, () -> {
//            documentoService.downloadPdfAsBase64(mockUid, "atestadoMatricula", mockSenha);
//        });
//
//        assertEquals("Usuário não possui vínculo ativo", exception.getMessage());
//    }
//
//
//    @Test
//    public void testDownloadPdfAsBase64Unauthorized() throws Exception {
//        // Mocking an unauthorized login attempt
//        setupMockHttpClient(false, "");
//
//        // Expects UnauthorizedException
//        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
//            documentoService.downloadPdfAsBase64(mockUid, "historico", mockSenha);
//        });
//
//        assertEquals("Falha ao realizar login no SIGAA", exception.getMessage());
//    }
//
//    @Test
//    public void testDownloadPdfAsBase64WithNullArguments() {
//        // Expects IllegalArgumentException when arguments are null
//        assertThrows(NullPointerException.class, () -> documentoService.downloadPdfAsBase64(null, "historico", mockSenha));
//        assertThrows(NullPointerException.class, () -> documentoService.downloadPdfAsBase64(mockUid, null, mockSenha));
//        assertThrows(NullPointerException.class, () -> documentoService.downloadPdfAsBase64(mockUid, "historico", null));
//    }
//
//    // Utility method to setup the OkHttpClient mock behavior
//    private void setupMockHttpClient(boolean isLoginSuccessful, String responseBody) throws IOException {
//        when(client.newCall(any())).thenReturn(mockCall);
//
//        when(mockCall.execute()).thenAnswer((Answer<Response>) invocation -> {
//            Response.Builder responseBuilder = new Response.Builder()
//                    .request(new Request.Builder().url("https://sig.ifrs.edu.br").build())
//                    .protocol(Protocol.HTTP_1_1)
//                    .code(isLoginSuccessful ? 200 : 401)
//                    .message(isLoginSuccessful ? "OK" : "Unauthorized");
//
//            if (isLoginSuccessful) {
//                responseBuilder.body(ResponseBody.create(responseBody, MediaType.get("text/html")));
//            } else {
//                responseBuilder.body(ResponseBody.create("", MediaType.get("text/html")));
//            }
//
//            return responseBuilder.build();
//        });
//    }
//}

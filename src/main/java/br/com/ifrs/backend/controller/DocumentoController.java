package br.com.ifrs.backend.controller;

import br.com.ifrs.backend.exception.UnauthorizedException;
import br.com.ifrs.backend.model.DocumentoRequest;
import br.com.ifrs.backend.model.Login;
import br.com.ifrs.backend.service.DocumentoService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/documento")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DocumentoController {
    private static final Logger logger = Logger.getLogger(DocumentoController.class.getName());
    private final DocumentoService documentoService;
    public DocumentoController(DocumentoService documentoService) { this.documentoService = documentoService; }

    @POST
    public Response getDocumento(@HeaderParam("Authorization") String token, DocumentoRequest documentoRequest) {
        if (token == null || token.isEmpty()) {
            logger.log(Level.WARNING, "Token de autenticação não fornecido!");
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token de autenticação não fornecido!").build();
        }
        Map<String, String> response = new HashMap<>();
        try{
            //String pdfbase64 = documentoService.downloadPdfAsBase64();
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String uid = decodedToken.getUid();
            String base64Pdf = documentoService.downloadPdfAsBase64(uid, documentoRequest.tipo(), documentoRequest.senha());
            response.put("pdfbase64", base64Pdf);
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (UnauthorizedException e) {
            response.put("mensagem", e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(response)
                    .build();
        } catch (FirebaseAuthException e) {
            logger.log(Level.WARNING, "Token de autenticação inválido!");
            response.put("mensagem", "Token de autenticação inválido!");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(response)
                    .build();
        }catch (Exception e) {
            response.put("mensagem", "Erro interno do servidor");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
        }
    }
}

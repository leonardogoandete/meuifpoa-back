package br.com.ifrs.backend.controller;

import br.com.ifrs.backend.model.Login;
import br.com.ifrs.backend.service.DocumentoService;
import com.google.firebase.auth.FirebaseAuth;
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

    @GET
    public Response getDocumento(@HeaderParam("Authorization") String token, Login login) {
        if (token == null || token.isEmpty()) {
            logger.log(Level.WARNING, "Token de autenticação não fornecido!");
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token de autenticação não fornecido!").build();
        }

        try{
            //String pdfbase64 = documentoService.downloadPdfAsBase64();
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String uid = decodedToken.getUid();
            documentoService.downloadPdfAsBase64(uid, login.getSenha());
            return Response.status(Response.Status.OK).build();
        }catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}

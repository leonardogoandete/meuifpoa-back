package br.com.ifrs.backend.controller;

import br.com.ifrs.backend.exception.UnauthorizedException;
import br.com.ifrs.backend.model.DocumentoRequest;
import br.com.ifrs.backend.service.DocumentoService;
import io.quarkus.security.Authenticated;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

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
    @Authenticated
    public Response getDocumento(@Context SecurityContext securityContext, DocumentoRequest documentoRequest) {
        String userId = securityContext.getUserPrincipal().getName();
        Map<String, String> response = new HashMap<>();
        try{
            String base64Pdf = documentoService.downloadPdfAsBase64(userId, documentoRequest.tipo(), documentoRequest.senha());
            response.put("pdfbase64", base64Pdf);
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (UnauthorizedException e) {
            response.put("mensagem", e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(response)
                    .build();
        }
        catch (Exception e) {
            response.put("mensagem", "Erro interno do servidor");
            logger.log(Level.SEVERE, "Erro interno do servidor", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
        }
    }
}

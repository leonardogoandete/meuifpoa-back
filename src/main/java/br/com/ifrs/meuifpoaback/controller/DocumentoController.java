package br.com.ifrs.meuifpoaback.controller;

import br.com.ifrs.meuifpoaback.exception.UnauthorizedException;
import br.com.ifrs.meuifpoaback.exception.VinculoBusinessException;
import br.com.ifrs.meuifpoaback.model.DocumentoRequest;
import br.com.ifrs.meuifpoaback.service.DocumentoService;
import io.quarkus.security.Authenticated;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A classe DocumentoController é responsável por gerenciar as requisições relacionadas a documentos.
 */
@Path("/documento")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DocumentoController {
    private static final Logger logger = Logger.getLogger(DocumentoController.class.getName());
    private final DocumentoService documentoService;

    /**
     * Instancia um novo controlador de documentos.
     *
     * @param documentoService o serviço de documentos
     */
    public DocumentoController(DocumentoService documentoService) { this.documentoService = documentoService; }

    /**
     * Obtém um documento em formato PDF codificado em base64.
     *
     * @param securityContext  o contexto de segurança
     * @param documentoRequest a requisição do documento
     * @return o documento em formato PDF codificado em base64
     */
    @POST
    @Operation(summary = "Obter documento PDF em base64")
    @Authenticated
    public Response getDocumento(@Context SecurityContext securityContext, DocumentoRequest documentoRequest) {
        String userId = securityContext.getUserPrincipal().getName();
        Map<String, String> response = new HashMap<>();
        try {
            if (documentoRequest.tipo() == null ||
                documentoRequest.senha() == null ||
                documentoRequest.tipo().isEmpty() ||
                documentoRequest.senha().isEmpty()) {
                throw new IllegalArgumentException("Argumentos nulos");
            }

            String base64Pdf = documentoService.downloadPdfAsBase64(userId, documentoRequest.tipo(), documentoRequest.senha());
            response.put("pdfbase64", base64Pdf);
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (IllegalArgumentException e) {
            response.put("mensagem", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(response)
                    .build();
        } catch (VinculoBusinessException e) {
            response.put("mensagem", e.getMessage());
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity(response)
                    .build();
        } catch (UnauthorizedException e) {
            response.put("mensagem", e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(response)
                    .build();
        } catch (Exception e) {
            response.put("mensagem", "Erro interno do servidor");
            logger.log(Level.SEVERE, "Erro interno do servidor", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
        }
    }
}

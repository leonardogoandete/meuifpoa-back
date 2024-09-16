package br.com.ifrs.backend.controller;

import br.com.ifrs.backend.service.DocumentoService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Path("/documento")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DocumentoController {
    private static final Logger logger = Logger.getLogger(DocumentoController.class.getName());
    private final DocumentoService documentoService;
    public DocumentoController(DocumentoService documentoService) { this.documentoService = documentoService; }

    @GET
    public Response getDocumento() {
        try{
            String pdfbase64 = documentoService.downloadPdfAsBase64();
            Map<String, Object> model = new HashMap<>();
            model.put("pdfbase64", pdfbase64);
            return Response.status(Response.Status.OK).entity(model).build();
        }catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}

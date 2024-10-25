package br.com.ifrs.meuifpoaback.controller;


import br.com.ifrs.meuifpoaback.model.Edital;
import br.com.ifrs.meuifpoaback.model.Noticia;
import br.com.ifrs.meuifpoaback.service.EditalService;
import br.com.ifrs.meuifpoaback.service.NoticiaService;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A classe EditalController é responsável por gerenciar as requisições relacionadas a editais.
 */
@Path("/editais")
public class EditalController {

    private static final Logger logger = Logger.getLogger(EditalController.class.getName());
    /**
     * O serviço de notícias.
     */
    private final EditalService editalService;

    /**
     * Construtor da classe.
     *
     * @param editalService o serviço de notícias
     */
    public EditalController(EditalService editalService) {
        this.editalService = editalService;
    }

    /**
     * Obtém notícias com base nos parâmetros fornecidos.
     *
     * @param limit  o limite de notícias a serem retornadas
     * @param filter o filtro a ser aplicado nas notícias
     * @return a resposta contendo a lista de notícias
     */
    @POST
    @Operation(summary = "Obter notícias utilizando parametros")
    @APIResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Edital.class)
            )
    )
    @PermitAll
    public Response obterEditalComFiltro(
            @Parameter(required = false,
                    schema = @Schema(type = SchemaType.INTEGER),
                    description = "O limite de editais a serem retornadas"
            ) @QueryParam("limit") int limit,
            @Parameter(required = false,
                    schema = @Schema(type = SchemaType.STRING),
                    description = "O filtro a ser aplicado nas buscas de editais"
            )
            @QueryParam("filter") String filter){
        try{
            List<Edital> editais = editalService.obterEditais(limit, filter);
            logger.log(Level.INFO, "Consultando editais");
            return Response.status(Response.Status.OK)
                    .entity(editais)
                    .build();
        }catch (Exception e){
            logger.log(Level.WARNING, "Erro ao consultar editais: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao consultar editais!")
                    .build();
        }
    }

    /**
     * Obtem uma lista de editais.
     * @return a resposta contendo a lista de editais
    */
    @GET
    @Operation(summary = "Obter todas os editais")
    @APIResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Edital.class)
            )
    )
    @PermitAll
    public Response obterTodasNoticias(){
        try{
            List<Edital> editais = editalService.obterEditais(0,null);
            logger.log(Level.INFO, "Consultando todas as notícias");
            return Response.status(Response.Status.OK)
                    .entity(editais)
                    .build();
        }catch (Exception e){
            logger.log(Level.WARNING, "Erro ao consultar todas os editais: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao consultar todas os editais!")
                    .build();
        }
    }
}

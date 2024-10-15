package br.com.ifrs.meuifpoaback.controller;


import br.com.ifrs.meuifpoaback.service.NoticiaService;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import br.com.ifrs.meuifpoaback.model.Noticia;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A classe NoticiaController é responsável por gerenciar as requisições relacionadas a notícias.
 */
@Path("/noticias")
public class NoticiaController {

    private static final Logger logger = Logger.getLogger(NoticiaController.class.getName());
    private final NoticiaService noticiaService;

    /**
     * Instancia um novo controlador de notícias.
     *
     * @param noticiaService o serviço de notícias
     */
    public NoticiaController(NoticiaService noticiaService) {
        this.noticiaService = noticiaService;
    }

    /**
     * Obtém notícias com base nos parâmetros fornecidos.
     *
     * @param limit  o limite de notícias a serem retornadas
     * @param filter o filtro a ser aplicado nas notícias
     * @return a resposta contendo a lista de notícias
     */
    @POST
    @Operation(summary = "Obter notícias")
    @PermitAll
    public Response obterNoticiasComFiltro(@QueryParam("limit") int limit, @QueryParam("filter") String filter){
        try{
            List<Noticia> noticias = noticiaService.obterNoticias(limit, filter);
            logger.log(Level.INFO, "Consultando notícias");
            return Response.status(Response.Status.OK)
                    .entity(noticias)
                    .build();
        }catch (Exception e){
            logger.log(Level.WARNING, "Erro ao consultar notícias: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao consultar notícias!")
                    .build();
        }
    }

    /**
     * Obtem uma lista de notícias.
     * @return a resposta contendo a lista de notícias
    */
    @GET
    @Operation(summary = "Obter todas as notícias")
    @PermitAll
    public Response obterTodasNoticias(){
        try{
            List<Noticia> noticias = noticiaService.obterNoticias(50,null);
            logger.log(Level.INFO, "Consultando todas as notícias");
            return Response.status(Response.Status.OK)
                    .entity(noticias)
                    .build();
        }catch (Exception e){
            logger.log(Level.WARNING, "Erro ao consultar todas as notícias: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao consultar todas as notícias!")
                    .build();
        }
    }


}

package br.com.ifrs.backend.controller;


import br.com.ifrs.backend.service.NoticiaService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import br.com.ifrs.backend.model.Noticia;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@Path("/noticias")
public class NoticiaController {

    private static final Logger logger = Logger.getLogger(NoticiaController.class.getName());
    private final NoticiaService noticiaService;

    public NoticiaController(NoticiaService noticiaService) {
        this.noticiaService = noticiaService;
    }

    @POST
    public Response obterNoticias(@QueryParam("limit") int limit, @QueryParam("filter") String filter){
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
}

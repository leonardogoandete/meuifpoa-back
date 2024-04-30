package controller;


import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import model.Noticia;
import service.NoticiaService;

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
        List<Noticia> noticias = noticiaService.obterNoticias(limit, filter);
        logger.log(Level.INFO, "Consultando notícias");
        return Response.status(Response.Status.OK)
                .entity(noticias)
                .build();
    }

}

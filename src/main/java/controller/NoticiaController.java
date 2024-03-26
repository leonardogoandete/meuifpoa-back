package controller;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
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

    @GET
    public Response obterNoticias(){
        List<Noticia> noticias = noticiaService.obterNoticias();
        logger.log(Level.INFO, "Consultando notícias");
        return Response.status(Response.Status.OK)
                .entity(noticias)
                .build();
    }

}

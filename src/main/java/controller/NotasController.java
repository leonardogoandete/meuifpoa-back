package controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.*;
import java.util.List;
import model.Login;
import model.Notas;
import model.Usuario;
import service.NotasService;


import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/notas")
@Consumes(MediaType.APPLICATION_JSON)
public class NotasController {
    private static final Logger logger = Logger.getLogger(NotasController.class.getName());
    private final NotasService notasService;
    public NotasController(NotasService notasService){
        this.notasService = notasService;
    }


    @POST()
    @Produces
    public Response minhasNotas(Login login){
        List<Notas> notas = notasService.obterNotas(login);
        logger.log(Level.INFO, "Consultando notas no SIGAA");
        return Response.status(jakarta.ws.rs.core.Response.Status.OK).entity(notas).build();
    }

    @POST()
    @Path("/usuario")
    @Produces
    public Response dadosUsuario(Login login){
        Usuario u = notasService.obterDadosUsuario(login);
        logger.log(Level.INFO, "Consultando informações do usuario no SIGAA");
        return Response.status(jakarta.ws.rs.core.Response.Status.OK).entity(u).build();
    }
}

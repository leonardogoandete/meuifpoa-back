package controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.*;
import java.util.List;
import model.Login;
import model.Notas;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import service.NotasService;


import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/notas")
@Consumes(MediaType.APPLICATION_JSON)
@SecurityScheme(scheme = "Bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT")
public class NotasController {
    private static final Logger logger = Logger.getLogger(NotasController.class.getName());
    private final NotasService notasService;
    public NotasController(NotasService notasService){
        this.notasService = notasService;
    }


    @POST()
    @Produces
    @RolesAllowed("USUARIO")
    public Response minhasNotas(Login login){
        List<Notas> notas = notasService.obterNotas(login);
        logger.log(Level.INFO, "Consultando notas no SIGAA");
        return Response.status(Response.Status.OK).entity(notas).build();
    }

}

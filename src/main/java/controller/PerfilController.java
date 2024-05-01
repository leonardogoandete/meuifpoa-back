package controller;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.Login;
import model.Perfil;
import service.PerfilService;

import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/perfil")
@Consumes(MediaType.APPLICATION_JSON)
public class PerfilController {

    private static final Logger logger = Logger.getLogger(PerfilController.class.getName());
    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @POST()
    public Response dadosUsuario(Login login){
        Perfil u = perfilService.obterDadosUsuario(login);
        logger.log(Level.INFO, "Consultando informações do usuario no SIGAA");
        return Response.status(Response.Status.OK).entity(u).build();
    }
}

package controller;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.Login;
import model.DadosUsuario;
import service.UsuarioService;

import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/usuario")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioController {

    private static final Logger logger = Logger.getLogger(UsuarioController.class.getName());
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @POST()
    public Response dadosUsuario(Login login){
        DadosUsuario u = usuarioService.obterDadosUsuario(login);
        logger.log(Level.INFO, "Consultando informações do usuario no SIGAA");
        return Response.status(jakarta.ws.rs.core.Response.Status.OK).entity(u).build();
    }
}

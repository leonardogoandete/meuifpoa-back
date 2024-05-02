package controller;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.Login;
import model.Perfil;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.jboss.resteasy.annotations.Body;
import service.PerfilService;

import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/perfil")
@Consumes(MediaType.APPLICATION_JSON)
@SecurityScheme(scheme = "Bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT")
public class PerfilController {

    private static final Logger logger = Logger.getLogger(PerfilController.class.getName());
    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @POST()
    @RolesAllowed("USUARIO")
    public Response dadosUsuario(Login login){
        Perfil u = perfilService.obterDadosUsuario(login);
        logger.log(Level.INFO, "Consultando informações do usuario no SIGAA");
        return Response.status(Response.Status.OK).entity(u).build();
    }
}

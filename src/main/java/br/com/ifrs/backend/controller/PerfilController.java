package br.com.ifrs.backend.controller;

import br.com.ifrs.backend.exception.UnauthorizedException;
import br.com.ifrs.backend.model.Login;
import br.com.ifrs.backend.model.Perfil;
import br.com.ifrs.backend.service.PerfilService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
    public Response dadosUsuario(@HeaderParam("Authorization") String token, Login login) {
        if (token == null || token.isEmpty()) {
            logger.log(Level.WARNING, "Token de autenticação não fornecido!");
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token de autenticação não fornecido!").build();
        }
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String uid = decodedToken.getUid();
            Perfil u = perfilService.obterDadosUsuario(uid, login.getSenha());
            logger.log(Level.INFO, "Consultando informações do usuario no SIGAA");
            return Response.status(Response.Status.OK).entity(u).build();
        }catch (UnauthorizedException e){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token de autenticação não fornecido----!").build();
        }catch (RuntimeException e){
            logger.log(Level.WARNING, "Erro ao consultar informações do usuario: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao consultar informações do usuario!").build();
        }catch (FirebaseAuthException e){
            logger.log(Level.WARNING, "Erro ao decodificar o token de autenticação: " + e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity("Erro ao decodificar token!").build();
        }catch (Exception e){
            logger.log(Level.WARNING, "Erro ao consultar informações do usuario: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao consultar informações do usuario!").build();
        }
    }
}

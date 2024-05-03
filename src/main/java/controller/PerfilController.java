package controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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
    public Response dadosUsuario(@HeaderParam("Authorization") String mToken) throws FirebaseAuthException {
        logger.log(Level.INFO,"Token:"+ mToken);


        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(mToken);
        String uid = decodedToken.getUid();


        logger.log(Level.INFO, "UUID: " + decodedToken.getUid());

        Perfil u = perfilService.obterDadosUsuario(uid);
        logger.log(Level.INFO, "Consultando informações do usuario no SIGAA");
        return Response.status(Response.Status.OK).entity(u).build();
    }
}

package controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
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
public class NotasController {
    private static final Logger logger = Logger.getLogger(NotasController.class.getName());
    private final NotasService notasService;
    public NotasController(NotasService notasService){
        this.notasService = notasService;
    }


    @POST()
    public Response minhasNotas(@HeaderParam("Authorization") String mToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(mToken);
        String uid = decodedToken.getUid();
        List<Notas> notas = notasService.obterNotas(uid);
        logger.log(Level.INFO, "Consultando notas no SIGAA");
        return Response.status(Response.Status.OK).entity(notas).build();
    }

}

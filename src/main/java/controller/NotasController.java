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
import model.Notas;
import service.NotasService;

import java.util.List;
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
    public Response minhasNotas(@HeaderParam("Authorization") String mToken) {
        if (mToken == null || mToken.isEmpty()) {
            logger.log(Level.WARNING, "Token de autenticação não fornecido!");
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token de autenticação não fornecido!").build();
        }
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(mToken);
            String uid = decodedToken.getUid();
            List<Notas> notas = notasService.obterNotas(uid);
            logger.log(Level.INFO, "Consultando notas no SIGAA");
            return Response.status(Response.Status.OK).entity(notas).build();
        }catch (FirebaseAuthException e){
            logger.log(Level.WARNING, "Erro ao decodificar o token de autenticação: " + e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity("Erro ao decodificar token!").build();
        }catch (Exception e){
            logger.log(Level.WARNING, "Erro ao consultar notas: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao consultar notas!").build();
        }

    }

}

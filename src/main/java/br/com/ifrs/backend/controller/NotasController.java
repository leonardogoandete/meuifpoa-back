package br.com.ifrs.backend.controller;

import br.com.ifrs.backend.service.NotasService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import br.com.ifrs.backend.model.Login;
import br.com.ifrs.backend.model.Notas;
import br.com.ifrs.backend.exception.UnauthorizedException;

import java.util.List;
import java.util.logging.Logger;

@Path("/notas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NotasController {

    private static final Logger logger = Logger.getLogger(NotasController.class.getName());
    private final NotasService notasService;

    public NotasController(NotasService notasService) {
        this.notasService = notasService;
    }

    @POST
    public Response minhasNotas(@HeaderParam("Authorization") String token, Login login) {
        if (token == null || token.isEmpty()) {
            logger.warning("Token de autenticação não fornecido!");
            throw new UnauthorizedException("Token de autenticação não fornecido!");
        }

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String uid = decodedToken.getUid();

            logger.info("Consultando notas no SIGAA para UID: " + uid);

            List<Notas> notas = notasService.obterNotas(uid, login.getSenha());

            return Response.ok(notas).build();
        } catch (FirebaseAuthException e) {
            logger.warning("Erro ao decodificar o token de autenticação: " + e.getMessage());
            throw new UnauthorizedException("Erro ao decodificar o token de autenticação: " + e.getMessage());
        }
    }
}

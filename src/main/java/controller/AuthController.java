package controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.Login;
import service.AuthService;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    AuthService authService;

    @POST
    public Response login(Login login) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        if (login.login() == null || login.login().isEmpty() || login.senha() == null || login.senha().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String token = authService.validarCredenciais(login);
        if (token != null) {
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return Response.status(Response.Status.OK).entity(response).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}

package br.com.ifrs.meuifpoaback.configuration;

import br.com.ifrs.meuifpoaback.exception.UnauthorizedException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import java.util.logging.Logger;

/**
 * Classe que implementa um filtro para interceptar as requisições e validar o token JWT do Firebase.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
@ApplicationScoped
public class FirebaseTokenFilter implements ContainerRequestFilter {

    private static final Logger logger = Logger.getLogger(FirebaseTokenFilter.class.getName());

    /**
     * Método que intercepta as requisições para autenticação.
     *
     * @param requestContext contexto da requisição
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        logger.info("Interceptando requisição para autenticação");

        if (requestContext.getUriInfo().getPath().contains("noticias")
                || requestContext.getUriInfo().getPath().contains("auth")
                || requestContext.getUriInfo().getPath().contains("/test")
                || requestContext.getUriInfo().getPath().contains("editais")) {
            logger.info("Endpoint liberado: " + requestContext.getUriInfo().getPath());
            return;
        }



        String authHeader = requestContext.getHeaderString("Authorization");
        logger.info("Cabeçalho Authorization: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Token JWT ausente ou inválido.");
        }

        String idToken = authHeader.substring(7); // Remove "Bearer "

        try {
            // Verifica o token do Firebase
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();

            logger.info("Token válido para o UID: " + uid);

            // Anexa o UID ao contexto de segurança
            requestContext.setSecurityContext(new FirebaseSecurityContext(uid, email));

        } catch (Exception e) {
            logger.severe("Token JWT inválido: " + e.getMessage());
            throw new UnauthorizedException("Token JWT inválido: " + e.getMessage());
        }
    }
}

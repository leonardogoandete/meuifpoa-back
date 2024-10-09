package br.com.ifrs.meuifpoaback.configuration;

import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * Classe que representa o contexto de segurança do Firebase.
 */
public class FirebaseSecurityContext implements SecurityContext {

    private final String uid;

    /**
     * Instancia um novo contexto de segurança do Firebase.
     *
     * @param uid o UID do usuário
     */
    public FirebaseSecurityContext(String uid) {
        this.uid = uid;
    }

    /**
     * Obtém o principal do usuário.
     *
     * @return o principal do usuário
     */
    @Override
    public Principal getUserPrincipal() {
        return () -> uid;
    }

    /**
     * Verifica se o usuário possui um determinado papel.
     *
     * @param role o papel a ser verificado
     * @return false, pois a verificação de papéis não é suportada
     */
    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    /**
     * Verifica se a conexão é segura.
     *
     * @return true, pois a conexão é sempre considerada segura
     */
    @Override
    public boolean isSecure() {
        return true;
    }

    /**
     * Obtém o esquema de autenticação.
     *
     * @return o esquema de autenticação, que é "JWT"
     */
    @Override
    public String getAuthenticationScheme() {
        return "JWT";
    }
}

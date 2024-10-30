package br.com.ifrs.meuifpoaback.configuration;

import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * Classe que representa o contexto de segurança do Firebase.
 */
public class FirebaseSecurityContext implements SecurityContext {

    /**
     * O UID do usuário no Firebase.
     */
    private final String uid;

    /**
     * O email do usuário no Firebase.
     */
    private String email;

    /**
     * Construtor que recebe o UID do usuário.
     *
     * @param uid o UID do usuário
     */
    public FirebaseSecurityContext(String uid, String email) {
        this.uid = uid;
        this.email = email;
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

    /**
     * Obtém o email do usuário.
     *
     * @return o email do usuário
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define o email do usuário.
     *
     * @param email o email do usuário
     */
    public void setEmail(String email) {
        this.email = email;
    }
}

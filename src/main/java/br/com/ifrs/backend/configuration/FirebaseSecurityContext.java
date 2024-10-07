package br.com.ifrs.backend.configuration;

import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * The type Firebase security context.
 */
public class FirebaseSecurityContext implements SecurityContext {

    private final String uid;

    /**
     * Instantiates a new Firebase security context.
     *
     * @param uid the uid
     */
    public FirebaseSecurityContext(String uid) {
        this.uid = uid;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> uid;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public boolean isSecure() {
        return true;
    }

    @Override
    public String getAuthenticationScheme() {
        return "JWT";
    }
}

//package br.com.ifrs.backend.configuration;
//
//import jakarta.ws.rs.core.SecurityContext;
//import java.security.Principal;
//
//public class FirebaseSecurityContext implements SecurityContext {
//
//    private final String uid;
//
//    public FirebaseSecurityContext(String uid) {
//        this.uid = uid;
//    }
//
//    @Override
//    public Principal getUserPrincipal() {
//        return () -> uid;
//    }
//
//    @Override
//    public boolean isUserInRole(String role) {
//        return false;
//    }
//
//    @Override
//    public boolean isSecure() {
//        return true;
//    }
//
//    @Override
//    public String getAuthenticationScheme() {
//        return "JWT";
//    }
//}

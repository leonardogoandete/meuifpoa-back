package br.com.ifrs.meuifpoaback.model;

/**
 * Classe que representa a resposta da autenticação no Firebase.
 * @param idToken token de autenticação
 */
public record FirebaseAuthResponse(String idToken) {
}

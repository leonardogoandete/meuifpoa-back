package br.com.ifrs.meuifpoaback.model;

/**
 * Classe que representa a requisição de autenticação no Firebase.
 * @param email email do usuário
 * @param password senha do usuário
 * @param returnSecureToken flag que indica se o token deve ser retornado
 */
public record FirebaseAuthRequest(String email, String password, boolean returnSecureToken) {
}

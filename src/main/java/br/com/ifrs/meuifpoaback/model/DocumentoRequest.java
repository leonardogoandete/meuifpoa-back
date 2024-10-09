package br.com.ifrs.meuifpoaback.model;

/**
 * A classe DocumentoRequest representa uma solicitação de documento.
 * @param tipo o tipo de documento a ser solicitado
 * @param senha a senha do usuário para o SIGAA
 */
public record DocumentoRequest (String tipo, String senha) {}

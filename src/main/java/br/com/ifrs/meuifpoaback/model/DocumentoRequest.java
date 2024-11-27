package br.com.ifrs.meuifpoaback.model;

/**
 * O record DocumentoRequest representa a requisição de um documento.
 * @param tipo o tipo do documento
 * @param senha a senha do aluno
 */
public record DocumentoRequest (String tipo, String senha) {}

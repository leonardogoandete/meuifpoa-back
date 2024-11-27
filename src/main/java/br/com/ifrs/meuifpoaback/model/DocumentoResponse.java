package br.com.ifrs.meuifpoaback.model;

/**
 * O record DocumentoResponse representa a resposta de um documento.
 * @param pdfbase64 o documento em formato PDF codificado em base64
 */
public record DocumentoResponse(String pdfbase64) {}

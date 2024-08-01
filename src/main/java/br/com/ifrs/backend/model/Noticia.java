package br.com.ifrs.backend.model;

public record Noticia(String link,
                      String titulo,
                      String resumo,
                      String dataPublicacao,
                      String horaPublicacao
) {}

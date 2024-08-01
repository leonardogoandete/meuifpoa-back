package br.com.ifrs.backend.model;

public record Perfil(String nomeDocente,
                     String matricula,
                     String curso,
                     String nivel,
                     String status,
                     String anoIngresso) {
}

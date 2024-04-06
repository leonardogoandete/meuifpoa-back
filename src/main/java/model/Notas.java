package model;

import com.fasterxml.jackson.databind.ObjectMapper;

public record Notas(
        String codigoDisciplina,
        String nomeDisciplina,
        String primeiraUnidade,
        String segundaUnidade,
        String notaRecuperacao,
        String notaFinal,
        String numeroFaltas,
        String situacao
) {
}

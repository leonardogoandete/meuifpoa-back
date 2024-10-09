package br.com.ifrs.meuifpoaback.model;

import java.util.List;

/**
 * Classe que representa os dados de sincronização.
 */
public class SyncData {
    private Perfil perfil;
    private List<Notas> notas;

    /**
     * Construtor para inicializar os dados de sincronização.
     *
     * @param perfil o perfil do docente
     * @param notas a lista de notas
     */
    public SyncData(Perfil perfil, List<Notas> notas) {
        this.perfil = perfil;
        this.notas = notas;
    }

    /**
     * Obtém o perfil do docente.
     *
     * @return o perfil do docente
     */
    public Perfil getPerfil() {
        return perfil;
    }

    /**
     * Obtém a lista de notas.
     *
     * @return a lista de notas
     */
    public List<Notas> getNotas() {
        return notas;
    }
}

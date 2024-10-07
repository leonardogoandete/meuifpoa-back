package br.com.ifrs.backend.model;

import java.util.List;

/**
 * The type Sync data.
 */
public class SyncData {
    private Perfil perfil;
    private List<Notas> notas;

    /**
     * Instantiates a new Sync data.
     *
     * @param perfil the perfil
     * @param notas  the notas
     */
    public SyncData(Perfil perfil, List<Notas> notas) {
        this.perfil = perfil;
        this.notas = notas;
    }

    /**
     * Gets perfil.
     *
     * @return the perfil
     */
    public Perfil getPerfil() {
        return perfil;
    }

    /**
     * Gets notas.
     *
     * @return the notas
     */
    public List<Notas> getNotas() {
        return notas;
    }
}

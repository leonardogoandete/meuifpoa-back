package br.com.ifrs.backend.model;

import java.util.List;

public class SyncData {
    private Perfil perfil;
    private List<Notas> notas;

    public SyncData(Perfil perfil, List<Notas> notas) {
        this.perfil = perfil;
        this.notas = notas;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public List<Notas> getNotas() {
        return notas;
    }
}

package br.com.ifrs.backend.model;

public class Noticia{
    private String link;
    private String titulo;
    private String resumo;
    private String dataPublicacao;
    private String horaPublicacao;

    public Noticia(){}

    public Noticia(String link, String titulo, String resumo, String dataPublicacao, String horaPublicacao) {
        this.link = link;
        this.titulo = titulo;
        this.resumo = resumo;
        this.dataPublicacao = dataPublicacao;
        this.horaPublicacao = horaPublicacao;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public String getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(String dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public String getHoraPublicacao() {
        return horaPublicacao;
    }

    public void setHoraPublicacao(String horaPublicacao) {
        this.horaPublicacao = horaPublicacao;
    }
}

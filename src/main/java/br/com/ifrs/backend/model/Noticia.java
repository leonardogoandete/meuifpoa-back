package br.com.ifrs.backend.model;

/**
 * The type Noticia.
 */
public class Noticia{
    private String link;
    private String titulo;
    private String resumo;
    private String dataPublicacao;
    private String horaPublicacao;

    /**
     * Instantiates a new Noticia.
     */
    public Noticia(){}

    /**
     * Instantiates a new Noticia.
     *
     * @param link           the link
     * @param titulo         the titulo
     * @param resumo         the resumo
     * @param dataPublicacao the data publicacao
     * @param horaPublicacao the hora publicacao
     */
    public Noticia(String link, String titulo, String resumo, String dataPublicacao, String horaPublicacao) {
        this.link = link;
        this.titulo = titulo;
        this.resumo = resumo;
        this.dataPublicacao = dataPublicacao;
        this.horaPublicacao = horaPublicacao;
    }

    /**
     * Gets link.
     *
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets link.
     *
     * @param link the link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Gets titulo.
     *
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Sets titulo.
     *
     * @param titulo the titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Gets resumo.
     *
     * @return the resumo
     */
    public String getResumo() {
        return resumo;
    }

    /**
     * Sets resumo.
     *
     * @param resumo the resumo
     */
    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    /**
     * Gets data publicacao.
     *
     * @return the data publicacao
     */
    public String getDataPublicacao() {
        return dataPublicacao;
    }

    /**
     * Sets data publicacao.
     *
     * @param dataPublicacao the data publicacao
     */
    public void setDataPublicacao(String dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    /**
     * Gets hora publicacao.
     *
     * @return the hora publicacao
     */
    public String getHoraPublicacao() {
        return horaPublicacao;
    }

    /**
     * Sets hora publicacao.
     *
     * @param horaPublicacao the hora publicacao
     */
    public void setHoraPublicacao(String horaPublicacao) {
        this.horaPublicacao = horaPublicacao;
    }
}

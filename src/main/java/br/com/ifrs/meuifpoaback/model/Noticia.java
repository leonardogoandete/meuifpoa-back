package br.com.ifrs.meuifpoaback.model;

/**
 * Classe que representa uma notícia.
 */
public class Noticia{
    private String link; // Link da notícia
    private String titulo; // Título da notícia
    private String resumo; // Resumo da notícia
    private String dataPublicacao; // Data de publicação da notícia
    private String horaPublicacao; // Hora de publicação da notícia

    /**
     * Construtor padrão.
     */
    public Noticia(){}

    /**
     * Construtor com parâmetros.
     *
     * @param link            o link da notícia
     * @param titulo          o título da notícia
     * @param resumo          o resumo da notícia
     * @param dataPublicacao  a data de publicação da notícia
     * @param horaPublicacao  a hora de publicação da notícia
     */
    public Noticia(String link, String titulo, String resumo, String dataPublicacao, String horaPublicacao) {
        this.link = link;
        this.titulo = titulo;
        this.resumo = resumo;
        this.dataPublicacao = dataPublicacao;
        this.horaPublicacao = horaPublicacao;
    }

    /**
     * Obtém o link da notícia.
     *
     * @return o link da notícia
     */
    public String getLink() {
        return link;
    }

    /**
     * Define o link da notícia.
     *
     * @param link o link da notícia
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Obtém o título da notícia.
     *
     * @return o título da notícia
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Define o título da notícia.
     *
     * @param titulo o título da notícia
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Obtém o resumo da notícia.
     *
     * @return o resumo da notícia
     */
    public String getResumo() {
        return resumo;
    }

    /**
     * Define o resumo da notícia.
     *
     * @param resumo o resumo da notícia
     */
    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    /**
     * Obtém a data de publicação da notícia.
     *
     * @return a data de publicação da notícia
     */
    public String getDataPublicacao() {
        return dataPublicacao;
    }

    /**
     * Define a data de publicação da notícia.
     *
     * @param dataPublicacao a data de publicação da notícia
     */
    public void setDataPublicacao(String dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    /**
     * Obtém a hora de publicação da notícia.
     *
     * @return a hora de publicação da notícia
     */
    public String getHoraPublicacao() {
        return horaPublicacao;
    }

    /**
     * Define a hora de publicação da notícia.
     *
     * @param horaPublicacao a hora de publicação da notícia
     */
    public void setHoraPublicacao(String horaPublicacao) {
        this.horaPublicacao = horaPublicacao;
    }
}

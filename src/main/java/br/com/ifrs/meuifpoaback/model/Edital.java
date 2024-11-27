package br.com.ifrs.meuifpoaback.model;

/**
 * Classe que representa um edital.
 */
public class Edital {
    /**
     * O link do edital.
     */
    private String link; // Link da notícia
    /**
     * O título do edital.
     */
    private String titulo; // Título da notícia
    /**
     * A data de publicação do edital.
     */
    private String dataPublicacaoEdital; // Data de publicação da notícia

    /**
     * Construtor padrão.
     */
    public Edital(){}

    /**
     * Construtor com parâmetros.
     *
     * @param link            o link do edital.
     * @param titulo          o título do edital.
     * @param dataPublicacaoEdital  a data de publicação do edital.
     */
    public Edital(String link, String titulo, String dataPublicacaoEdital) {
        this.link = link;
        this.titulo = titulo;
        this.dataPublicacaoEdital = dataPublicacaoEdital;
    }

    /**
     * Obtém o link do edital.
     *
     * @return o link do edital.
     */
    public String getLink() {
        return link;
    }

    /**
     * Define o link do edital.
     *
     * @param link o link do edital.
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Obtém o título do edital.
     *
     * @return o título do edital.
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Define o título do edital.
     *
     * @param titulo o título do edital.
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }


    /**
     * Obtém a data de publicação do edital.
     *
     * @return a data de publicação do edital.
     */
    public String getDataPublicacaoEdital() {
        return dataPublicacaoEdital;
    }

    /**
     * Define a data de publicação do edital.
     *
     * @param dataPublicacaoEdital a data de publicação dedital.
     */
    public void setDataPublicacaoEdital(String dataPublicacaoEdital) {
        this.dataPublicacaoEdital = dataPublicacaoEdital;
    }
}

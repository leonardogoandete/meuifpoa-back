package br.com.ifrs.meuifpoaback.model;
/**
 * A classe ObterToken representa a requisição de token.
 */
public class ObterToken {
    /**
     * O tipo de concessão.
     */
    private String grant_type;
    /**
     * O identificador do cliente.
     */
    private String client_id;
    /**
     * O segredo do cliente.
     */
    private String client_secret;

    /**
     * Construtor da classe.
     *
     * @param grant_type    o tipo de concessão
     * @param client_id     o identificador do cliente
     * @param client_secret o segredo do cliente
     */
    public ObterToken(String grant_type, String client_id, String client_secret) {
        this.grant_type = grant_type;
        this.client_id = client_id;
        this.client_secret = client_secret;
    }

    /**
     * Obtém o tipo de concessão.
     *
     * @return o tipo de concessão
     */
    public String getGrant_type() {
        return grant_type;
    }
    /**
     * Define o tipo de concessão.
     *
     * @param grant_type o tipo de concessão
     */
    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }
    /**
     * Obtém o identificador do cliente.
     *
     * @return o identificador do cliente
     */
    public String getClient_id() {
        return client_id;
    }
    /**
     * Define o identificador do cliente.
     *
     * @param client_id o identificador do cliente
     */
    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
    /**
     * Obtém o segredo do cliente.
     *
     * @return o segredo do cliente
     */
    public String getClient_secret() {
        return client_secret;
    }
    /**
     * Define o segredo do cliente.
     *
     * @param client_secret o segredo do cliente
     */
    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }
}

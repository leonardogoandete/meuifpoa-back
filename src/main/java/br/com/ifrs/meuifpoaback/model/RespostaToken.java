package br.com.ifrs.meuifpoaback.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A classe RespostaToken representa a requisição de token.
 */
public class RespostaToken {
    /**
     * O tipo de token.
     */
    @JsonProperty("token_type")
    private String token_type;
    /**
     * O tempo de expiração do token.
     */
    @JsonProperty("expires_in")
    private int expires_in;
    /**
     * O token de acesso.
     */
    @JsonProperty("access_token")
    private String access_token;

    /**
     * Obtém o tipo de concessão.
     *
     * @return o tipo de token
     */
    public String getTokenType() {
        return token_type;
    }
    /**
     * Define o tipo de token.
     *
     * @param token_type o tipo de token
     */
    public void setTokenType(String token_type) {
        this.token_type = token_type;
    }
    /**
     * Obtém o tempo de expiração do token.
     *
     * @return o tempo de expiração do token
     */
    public int getExpiresIn() {
        return expires_in;
    }
    /**
     * Define o tempo de expiração do token.
     *
     * @param expires_in o tempo de expiração do token
     */
    public void setExpiresIn(int expires_in) {
        this.expires_in = expires_in;
    }
    /**
     * Obtém o token de acesso.
     *
     * @return o token de acesso
     */
    public String getAccessToken() {
        return access_token;
    }
    /**
     * Define o token de acesso.
     *
     * @param access_token o token de acesso
     */
    public void setAccessToken(String access_token) {
        this.access_token = access_token;
    }

    /**
     * Retorna a representação em String do objeto.
     * @return a representação em String do objeto
     */
    @Override
    public String toString() {
        return "TokenResponse{" +
                "token_type='" + token_type + '\'' +
                ", expires_in=" + expires_in +
                ", access_token='" + access_token + '\'' +
                '}';
    }
}

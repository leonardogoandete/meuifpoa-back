package br.com.ifrs.backend.model;

/**
 * The type Login.
 */
public class Login{
    private String senha;

    /**
     * Instantiates a new Login.
     */
    public Login() {
    }

    /**
     * Instantiates a new Login.
     *
     * @param senha the senha
     */
    public Login(String senha) {
        this.senha = senha;
    }

    /**
     * Gets senha.
     *
     * @return the senha
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Sets senha.
     *
     * @param senha the senha
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }
}

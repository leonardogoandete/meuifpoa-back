package br.com.ifrs.backend.model;

/**
 * A classe Login.
 */
public class Login{
    private String senha;

    /**
     * Instancia um novo Login.
     */
    public Login() {
    }

    /**
     * Instancia um novo Login.
     *
     * @param senha a senha
     */
    public Login(String senha) {
        this.senha = senha;
    }

    /**
     * Obtém a senha.
     *
     * @return a senha
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Define a senha.
     *
     * @param senha a senha
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }
}

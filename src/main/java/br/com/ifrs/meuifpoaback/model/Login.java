package br.com.ifrs.meuifpoaback.model;

/**
 * A classe Login representa o login de um aluno
 */
public class Login{
    private String senha;

    /**
     * Construtor padrão.
     */
    public Login() {
    }

    /**
     * Construtor com parâmetros.
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

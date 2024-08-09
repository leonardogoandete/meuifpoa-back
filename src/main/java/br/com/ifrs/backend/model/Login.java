package br.com.ifrs.backend.model;

public class Login{
    private String senha;

    public Login() {
    }

    public Login(String senha) {
        this.senha = senha;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}

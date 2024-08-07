package br.com.ifrs.backend.model;

public class Login{
    private String senha;

    public Login(String senha) {
        this.senha = senha;
    }

    public String senha() {
        return senha;
    }

    public void senha(String senha) {
        this.senha = senha;
    }
}

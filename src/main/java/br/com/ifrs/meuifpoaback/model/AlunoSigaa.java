package br.com.ifrs.meuifpoaback.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * A classe AlunoSigaa representa o retorno do aluno da API do SIGAA.
 * Esta sendo utilizado a anotação @JsonIgnoreProperties para ignorar propriedades desnecessárias.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlunoSigaa {

    /**
     * Construtor default da classe.
     */
    public AlunoSigaa() {
    }

    /**
     * Construtor da classe.
     *
     * @param login o login do aluno
     */
    public AlunoSigaa(String login) {
        this.login = login;
    }

    /**
     * O login do aluno.
     */
    private String login;

    /**
     * O nome completo do aluno.
     */
    @JsonProperty("nome_completo")
    private String nomeCompleto;

    /**
     * O e-mail do aluno.
     */
    private String email;

    /**
     * A data de nascimento do aluno.
     */
    @JsonProperty("data_nascimento")
    private String dataNascimento;
    /**
     * O sexo do aluno.
     */
    private String sexo;
    /**
     * O ID da foto do aluno.
     */
    private String foto;
    /**
     * A data de cadastro do aluno.
     */
    @JsonProperty("data_cadastro")
    private String dataCadastro;

    /**
     * A unidade de Ensino que o aluno está vinculado.
     */
    @JsonProperty("unidade")
    private String unidade;

    /**
     * Obtém o login do aluno.
     *
     * @return o login do aluno
     */
    public String getLogin() {
        return login;
    }

    /**
     * Define o login do aluno.
     *
     * @param login o login do aluno
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Obtém o nome completo do aluno.
     *
     * @return o nome completo do aluno
     */
    public String getNomeCompleto() {
        return nomeCompleto;
    }

    /**
     * Define o nome completo do aluno.
     *
     * @param nomeCompleto o nome completo do aluno
     */
    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    /**
     * Obtém o e-mail do aluno.
     *
     * @return o e-mail do aluno
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define o e-mail do aluno.
     *
     * @param email o e-mail do aluno
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtém a data de nascimento do aluno.
     *
     * @return a data de nascimento do aluno
     */
    public String getDataNascimento() {
        return dataNascimento;
    }

    /**
     * Define a data de nascimento do aluno.
     *
     * @param dataNascimento a data de nascimento do aluno
     */
    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    /**
     * Obtém o sexo do aluno.
     *
     * @return o sexo do aluno
     */
    public String getSexo() {
        return sexo;
    }

    /**
     * Define o sexo do aluno.
     *
     * @param sexo o sexo do aluno
     */
    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    /**
     * Obtém o ID da foto do aluno.
     *
     * @return o ID da foto do aluno
     */
    public String getFoto() {
        return foto;
    }

    /**
     * Define o ID da foto do aluno.
     *
     * @param foto o ID da foto do aluno
     */
    public void setFoto(String foto) {
        this.foto = foto;
    }

    /**
     * Obtém a data de cadastro do aluno.
     *
     * @return a data de cadastro do aluno
     */
    public String getDataCadastro() {
        return dataCadastro;
    }

    /**
     * Define a data de cadastro do aluno.
     *
     * @param dataCadastro a data de cadastro do aluno
     */
    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    /**
     * Obtém a unidade de Ensino que o aluno está vinculado.
     *
     * @return a unidade de Ensino que o aluno está vinculado
     */
    public String getUnidade() {
        return unidade;
    }

    /**
     * Define a unidade de Ensino que o aluno está vinculado.
     *
     * @param unidade a unidade de Ensino que o aluno está vinculado
     */
    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }
}

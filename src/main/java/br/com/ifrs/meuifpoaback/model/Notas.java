package br.com.ifrs.meuifpoaback.model;

/**
 * A classe Notas representa as notas de um aluno em uma disciplina.
 */

public class Notas{
    /**
     * O código da disciplina.
     */
    private String codigoDisciplina;
    /**
     * O nome da disciplina.
     */
    private String nomeDisciplina;
    /**
     * A nota da primeira unidade.
     */
    private String primeiraUnidade;
    /**
     * A nota da segunda unidade.
     */
    private String segundaUnidade;
    /**
     * A nota de recuperação.
     */
    private String notaRecuperacao;
    /**
     * A nota final.
     */
    private String notaFinal;
    /**
     * O número de faltas.
     */
    private String numeroFaltas;
    /**
     * A situação do aluno na disciplina.
     * Exemplo: Aprovado, Reprovado.
     */
    private String situacao;

    /**
     * Construtor padrão.
     */
    public Notas() {
    }

    /**
     * Construtor com parâmetros.
     *
     * @param codigoDisciplina o código da disciplina
     * @param nomeDisciplina   o nome da disciplina
     * @param primeiraUnidade  a nota da primeira unidade
     * @param segundaUnidade   a nota da segunda unidade
     * @param notaRecuperacao  a nota de recuperação
     * @param notaFinal        a nota final
     * @param numeroFaltas     o número de faltas
     * @param situacao         a situação do aluno na disciplina
     */
    public Notas(String codigoDisciplina, String nomeDisciplina, String primeiraUnidade, String segundaUnidade, String notaRecuperacao, String notaFinal, String numeroFaltas, String situacao) {
        this.codigoDisciplina = codigoDisciplina;
        this.nomeDisciplina = nomeDisciplina;
        this.primeiraUnidade = primeiraUnidade;
        this.segundaUnidade = segundaUnidade;
        this.notaRecuperacao = notaRecuperacao;
        this.notaFinal = notaFinal;
        this.numeroFaltas = numeroFaltas;
        this.situacao = situacao;
    }

    /**
     * Obtém o código da disciplina.
     *
     * @return o código da disciplina
     */

    public String getCodigoDisciplina() {
        return codigoDisciplina;
    }

    /**
     * Define o código da disciplina.
     *
     * @param codigoDisciplina o código da disciplina
     */
    public void setCodigoDisciplina(String codigoDisciplina) {
        this.codigoDisciplina = codigoDisciplina;
    }

    /**
     * Obtém o nome da disciplina.
     *
     * @return o nome da disciplina
     */

    public String getNomeDisciplina() {
        return nomeDisciplina;
    }

    /**
     * Define o nome da disciplina.
     *
     * @param nomeDisciplina o nome da disciplina
     */

    public void setNomeDisciplina(String nomeDisciplina) {
        this.nomeDisciplina = nomeDisciplina;
    }

    /**
     * Obtém a nota da primeira unidade.
     *
     * @return a nota da primeira unidade
     */

    public String getPrimeiraUnidade() {
        return primeiraUnidade;
    }

    /**
     * Define a nota da primeira unidade.
     *
     * @param primeiraUnidade a nota da primeira unidade
     */

    public void setPrimeiraUnidade(String primeiraUnidade) {
        this.primeiraUnidade = primeiraUnidade;
    }

    /**
     * Obtém a nota da segunda unidade.
     *
     * @return a nota da segunda unidade
     */

    public String getSegundaUnidade() {
        return segundaUnidade;
    }

    /**
     * Define a nota da segunda unidade.
     *
     * @param segundaUnidade a nota da segunda unidade
     */

    public void setSegundaUnidade(String segundaUnidade) {
        this.segundaUnidade = segundaUnidade;
    }

    /**
     * Obtém a nota de recuperação.
     *
     * @return a nota de recuperação
     */

    public String getNotaRecuperacao() {
        return notaRecuperacao;
    }

    /**
     * Define a nota de recuperação.
     *
     * @param notaRecuperacao a nota de recuperação
     */

    public void setNotaRecuperacao(String notaRecuperacao) {
        this.notaRecuperacao = notaRecuperacao;
    }

    /**
     * Obtém a nota final.
     *
     * @return a nota final
     */

    public String getNotaFinal() {
        return notaFinal;
    }

    /**
     * Define a nota final.
     *
     * @param notaFinal a nota final
     */

    public void setNotaFinal(String notaFinal) {
        this.notaFinal = notaFinal;
    }

    /**
     * Obtém o número de faltas.
     *
     * @return o número de faltas
     */

    public String getNumeroFaltas() {
        return numeroFaltas;
    }

    /**
     * Define o número de faltas.
     *
     * @param numeroFaltas o número de faltas
     */

    public void setNumeroFaltas(String numeroFaltas) {
        this.numeroFaltas = numeroFaltas;
    }

    /**
     * Obtém a situação do aluno na disciplina.
     *
     * @return a situação do aluno na disciplina
     */

    public String getSituacao() {
        return situacao;
    }

    /**
     * Define a situação do aluno na disciplina.
     *
     * @param situacao a situação do aluno na disciplina
     */

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}



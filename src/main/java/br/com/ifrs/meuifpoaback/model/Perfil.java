package br.com.ifrs.meuifpoaback.model;

/**
 * Classe que representa o perfil de um docente.
 */
public class Perfil{
    private String nomeDocente;
    private String matricula;
    private String cpf;
    private String curso;
    private String nivel;
    private String status;
    private String anoIngresso;
    private String email;
    private String imgSrc;
    private String chObrigatoriaPendente;
    private String chOptativaPendente;
    private String chTotalCurriculo;
    private String chComplementarPendente;
    private String integralizado;

    /**
     * Construtor padrão.
     */
    public Perfil() {
    }

    /**
     * Construtor com parâmetros.
     *
     * @param nomeDocente o nome do docente
     * @param matricula a matrícula do docente
     * @param cpf o CPF do docente
     * @param curso o curso do docente
     * @param nivel o nível do docente
     * @param status o status do docente
     * @param anoIngresso o ano de ingresso do docente
     * @param email o email do docente
     * @param imgSrc a URL da imagem do docente
     */
    public Perfil(String nomeDocente, String matricula, String cpf, String curso, String nivel, String status, String anoIngresso, String email, String imgSrc){
        this.nomeDocente = nomeDocente;
        this.matricula = matricula;
        this.cpf = cpf;
        this.curso = curso;
        this.nivel = nivel;
        this.status = status;
        this.anoIngresso = anoIngresso;
        this.email = email;
        this.imgSrc = imgSrc;
    }

    /**
     * Construtor com todos os parâmetros.
     *
     * @param nomeDocente o nome do docente
     * @param matricula a matrícula do docente
     * @param cpf o CPF do docente
     * @param curso o curso do docente
     * @param nivel o nível do docente
     * @param status o status do docente
     * @param anoIngresso o ano de ingresso do docente
     * @param email o email do docente
     * @param imgSrc a URL da imagem do docente
     * @param chObrigatoriaPendente a carga horária obrigatória pendente
     * @param chOptativaPendente a carga horária optativa pendente
     * @param chTotalCurriculo a carga horária total do currículo
     * @param chComplementarPendente a carga horária complementar pendente
     * @param integralizado se o currículo está integralizado
     */
    public Perfil(String nomeDocente, String matricula, String cpf, String curso, String nivel, String status, String anoIngresso, String email, String imgSrc, String chObrigatoriaPendente, String chOptativaPendente, String chTotalCurriculo, String chComplementarPendente, String integralizado){
        this.nomeDocente = nomeDocente;
        this.matricula = matricula;
        this.cpf = cpf;
        this.curso = curso;
        this.nivel = nivel;
        this.status = status;
        this.anoIngresso = anoIngresso;
        this.email = email;
        this.imgSrc = imgSrc;
        this.chObrigatoriaPendente = chObrigatoriaPendente;
        this.chOptativaPendente = chOptativaPendente;
        this.chTotalCurriculo = chTotalCurriculo;
        this.chComplementarPendente = chComplementarPendente;
        this.integralizado = integralizado;
    }


    /**
     * Obtém o nome do docente.
     *
     * @return o nome do docente
     */
    public String getNomeDocente() {
        return nomeDocente;
    }

    /**
     * Define o nome do docente.
     *
     * @param nomeDocente o nome do docente
     */
    public void setNomeDocente(String nomeDocente) {
        this.nomeDocente = nomeDocente;
    }

    /**
     * Obtém a matrícula do docente.
     *
     * @return a matrícula do docente
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     * Define a matrícula do docente.
     *
     * @param matricula a matrícula do docente
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**
     * Obtém o CPF do docente.
     *
     * @return o CPF do docente
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Define o CPF do docente.
     *
     * @param cpf o CPF do docente
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    /**
     * Obtém o curso do docente.
     *
     * @return o curso do docente
     */
    public String getCurso() {
        return curso;
    }

    /**
     * Define o curso do docente.
     *
     * @param curso o curso do docente
     */
    public void setCurso(String curso) {
        this.curso = curso;
    }

    /**
     * Obtém o nível do docente.
     *
     * @return o nível do docente
     */
    public String getNivel() {
        return nivel;
    }

    /**
     * Define o nível do docente.
     *
     * @param nivel o nível do docente
     */
    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    /**
     * Obtém o status do docente.
     *
     * @return o status do docente
     */
    public String getStatus() {
        return status;
    }

    /**
     * Define o status do docente.
     *
     * @param status o status do docente
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Obtém o ano de ingresso do docente.
     *
     * @return o ano de ingresso do docente
     */
    public String getAnoIngresso() {
        return anoIngresso;
    }

    /**
     * Define o ano de ingresso do docente.
     *
     * @param anoIngresso o ano de ingresso do docente
     */
    public void setAnoIngresso(String anoIngresso) {
        this.anoIngresso = anoIngresso;
    }

    /**
     * Obtém o email do docente.
     *
     * @return o email do docente
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define o email do docente.
     *
     * @param email o email do docente
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtém a URL da imagem do docente.
     *
     * @return a URL da imagem do docente
     */
    public String getImgSrc() {
        return imgSrc;
    }

    /**
     * Define a URL da imagem do docente.
     *
     * @param imgSrc a URL da imagem do docente
     */
    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    /**
     * Obtém a carga horária obrigatória pendente.
     *
     * @return a carga horária obrigatória pendente
     */
    public String getChObrigatoriaPendente() {
        return chObrigatoriaPendente;
    }

    /**
     * Define a carga horária obrigatória pendente.
     *
     * @param chObrigatoriaPendente a carga horária obrigatória pendente
     */
    public void setChObrigatoriaPendente(String chObrigatoriaPendente) {
        this.chObrigatoriaPendente = chObrigatoriaPendente;
    }

    /**
     * Obtém a carga horária optativa pendente.
     *
     * @return a carga horária optativa pendente
     */
    public String getChOptativaPendente() {
        return chOptativaPendente;
    }

    /**
     * Define a carga horária optativa pendente.
     *
     * @param chOptativaPendente a carga horária optativa pendente
     */
    public void setChOptativaPendente(String chOptativaPendente) {
        this.chOptativaPendente = chOptativaPendente;
    }

    /**
     * Obtém a carga horária total do currículo.
     *
     * @return a carga horária total do currículo
     */
    public String getChTotalCurriculo() {
        return chTotalCurriculo;
    }

    /**
     * Define a carga horária total do currículo.
     *
     * @param chTotalCurriculo a carga horária total do currículo
     */
    public void setChTotalCurriculo(String chTotalCurriculo) {
        this.chTotalCurriculo = chTotalCurriculo;
    }

    /**
     * Obtém a carga horária complementar pendente.
     *
     * @return a carga horária complementar pendente
     */
    public String getChComplementarPendente() {
        return chComplementarPendente;
    }

    /**
     * Define a carga horária complementar pendente.
     *
     * @param chComplementarPendente a carga horária complementar pendente
     */
    public void setChComplementarPendente(String chComplementarPendente) {
        this.chComplementarPendente = chComplementarPendente;
    }

    /**
     * Obtém se o currículo está integralizado.
     *
     * @return se o currículo está integralizado
     */
    public String getIntegralizado() {
        return integralizado;
    }

    /**
     * Define se o currículo está integralizado.
     *
     * @param integralizado se o currículo está integralizado
     */
    public void setIntegralizado(String integralizado) {
        this.integralizado = integralizado;
    }
}

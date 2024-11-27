package br.com.ifrs.meuifpoaback.client;

import br.com.ifrs.meuifpoaback.model.AlunoSigaa;
import br.com.ifrs.meuifpoaback.model.ObterToken;
import br.com.ifrs.meuifpoaback.model.RespostaToken;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.Map;

/**
 * A interface SigaaClient representa o cliente REST para a API do SIGAA.
 */
@RegisterRestClient(configKey = "sigaa-api")
public interface SigaaClient {
    /**
     * Obtém o token de acesso.
     *
     * @param request a requisição de token
     * @return o token de acesso
     */
    @POST()
    @Path("/oauth/token")
    @Consumes("application/json")
    @Produces("application/json")
    RespostaToken getToken(ObterToken request);

    /**
     * Obtém um aluno do SIGAA.
     *
     * @param matricula     a matrícula do aluno
     * @param authorization a autorização
     * @return o aluno
     */
    @GET()
    @Path("/api/v1/sig/sigaa/alunos")
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, AlunoSigaa> getAluno(@QueryParam("matricula") String matricula,
                                     @HeaderParam("Authorization") String authorization);
}

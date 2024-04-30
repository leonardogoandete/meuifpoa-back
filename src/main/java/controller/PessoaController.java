package controller;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.DTO.PessoaDTO;
import service.PessoaService;

@Path("/register")
@Consumes(MediaType.APPLICATION_JSON)
public class PessoaController {

    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @POST
    public Response register(PessoaDTO pessoaDTO) {
        pessoaService.adicionarPessoa(pessoaDTO);
        return Response.ok().build();
    }

}

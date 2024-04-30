package service;

import jakarta.enterprise.context.ApplicationScoped;
import model.DTO.PessoaDTO;
import model.Pessoa;
import io.quarkus.elytron.security.common.BcryptUtil;
import repository.PessoaRepository;

import java.util.logging.Logger;

@ApplicationScoped
public class PessoaService {
    public static final Logger LOGGER = Logger.getLogger(PessoaService.class.getName());
    PessoaRepository pessoaRepository;

    public PessoaService() {
        pessoaRepository = new PessoaRepository();
    }


    public Pessoa buscarPessoaPorCpf(String cpf) {
        return pessoaRepository.findByCpf(cpf);
    }

    public void adicionarPessoa(PessoaDTO pessoaDTO) {
        String senhaHash = BcryptUtil.bcryptHash(pessoaDTO.senha());

        Pessoa pessoa = new Pessoa();
        pessoa.setCpf(pessoaDTO.cpf());
        pessoa.setNome(pessoaDTO.nome());
        pessoa.setEmail(pessoaDTO.email());
        pessoa.setSenha(senhaHash);
        pessoaRepository.persist(pessoa);
    }
}

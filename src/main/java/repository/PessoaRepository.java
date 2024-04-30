package repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import model.Pessoa;

@ApplicationScoped
public class PessoaRepository implements PanacheRepository<Pessoa> {
    public Pessoa findByCpf(String cpf) {
        return find("cpf", cpf).firstResult();
    }
}

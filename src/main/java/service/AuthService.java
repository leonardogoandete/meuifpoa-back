package service;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import model.Login;
import utils.SecurityUtil;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

@ApplicationScoped
public class AuthService {
    public static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private PessoaService pessoaService;

    public AuthService(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    public String validarCredenciais(Login login) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        String senhaRecebida = login.senha();
        String hashSenha = pessoaService.buscarPessoaPorCpf(login.login()).getSenha();
        if (SecurityUtil.verifyBCryptPassword(hashSenha, senhaRecebida)){
            return Jwt.issuer("https://localhost")
                    .upn(login.login())
                    .expiresAt(System.currentTimeMillis()+3600)
                    .sign();
        }
        return null;
    }
}

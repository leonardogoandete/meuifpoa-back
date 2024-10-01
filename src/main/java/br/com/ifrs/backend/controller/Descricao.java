package br.com.ifrs.backend.controller;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@OpenAPIDefinition(
        tags = {
                @Tag(name = "Documento", description = "Operações relacionadas a documentos"),
                @Tag(name = "Sincronização", description = "Operações relacionadas a sincronização de dados"),
                @Tag(name = "Notícia", description = "Operações relacionadas a notícias")
        },
        info = @Info(
                title = "IFRS Backend API",
                version = "1.0.0",
                contact = @Contact(
                        name = "Equipe de desenvolvimento",
                        email = "email@email.com"
                )
        ),
        servers = {
                @Server(url = "https://app.ifrs.edu.br/meuifpoa/"),
                @Server(url = "http://localhost:8080")

        }
)
public class Descricao extends Application {

}

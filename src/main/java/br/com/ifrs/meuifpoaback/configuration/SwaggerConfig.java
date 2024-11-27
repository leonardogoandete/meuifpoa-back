package br.com.ifrs.meuifpoaback.configuration;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;


@OpenAPIDefinition(
        info = @Info(
                title = "Meu IFPOA API",
                description = "API do aplicativo Meu IFPOA",
                version = "1.0.0"
        ),
        servers = {
                @Server(url = "https://app.poa.ifrs.edu.br/meuifpoa/"),
                @Server(url = "http://localhost:8080")
        }
)
public class SwaggerConfig extends Application {

}

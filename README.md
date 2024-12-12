# Projeto Backend

## Descrição
Este projeto é um serviço backend desenvolvido em Java utilizando o framework Quarkus. Ele inclui funcionalidades para manipulação de documentos e integração com serviços externos.
Serve como api para obter informações de documentos, notícias e notas de alunos e acessar essas informações através de endpoints.

## Tecnologias Utilizadas
- **Java**
- **Quarkus**
- **Maven**
- **Jsoup**
- **OkHttp**

## Estrutura do Projeto
### Diretórios
- `src/main/java/br/com/ifrs/meuifpoaback/service`: Contém as lógicas de negócio do projeto.  
- `src/main/java/br/com/ifrs/meuifpoaback/controller`: Contém os endpoints mapeados no projeto. 
- `src/main/java/br/com/ifrs/meuifpoaback/model`: Contém a definição dos modelos utilizados no projeto.
- `src/main/java/br/com/ifrs/meuifpoaback/configuration`: Contém as configurações do projeto. 
- `src/main/java/br/com/ifrs/meuifpoaback/client`: Contém os clientes para acesso a serviços externos.
- `src/main/java/br/com/ifrs/meuifpoaback/exception`: Contém as exceções personalizadas do projeto.
- `src/main/java/br/com/ifrs/meuifpoaback/utils`: Contém classes utilitárias.

## Configuração do Ambiente
1. **Pré-requisitos**:
    - JDK 17 ou superior
    - Maven 3.6 ou superior

2. **Clonar o repositório**:
    ```sh
    git clone https://github.com/leonardogoandete/projeto-backend.git
    cd projeto-meuifpoaback
    ```
3. **Configurar certificado para autenticação dos usuarios com o Firebase**:
    - Configurar o arquivo `application.properties` com as informações do Firebase adicionando as seguintes linhas:
    - Substituir `<ID_PROJETO>` pelo id do projeto no Firebase.
    ```
    quarkus.smallrye-jwt.enabled=true
    smallrye.jwt.verify.key.location=https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com
    mp.jwt.verify.issuer=https://securetoken.google.com/<ID_PROJETO>
    ```
4. **Configurar API externa**:
    - Configurar o arquivo `application.properties` com as informações da API externa adicionando as seguintes linhas:
    - Substituir `<GRANT_TYPE>`, `<CLIENT_ID da API do SIGAA>` e `<CLIENT_SECRET  da API do SIGAA>` pelas informações da API do SIGAA.
    - Substituir `<URL da API do SIGAA>` pela URL da API do SIGAA.
    ```
    sigaaApi.grant_type=<GRANT_TYPE>
    sigaaApi.client_id=<CLIENT_ID da API do SIGAA>
    sigaaApi.client_secret=<CLIENT_SECRET  da API do SIGAA>
    sigaa-api/mp-rest/url=<URL da API do SIGAA>
    sigaa-api/mp-rest/scope=javax.inject.Singleton
    ```
5. **Configurar credencial do Google SDK Admin**
    - Configurar a conta Google para utilizar o firebase conforme link [https://firebase.google.com/docs/admin/setup?hl=pt-br#initialize-sdk](https://firebase.google.com/docs/admin/setup?hl=pt-br#initialize-sdk).
    - Após obter o arquivo json, codifica-lo em base64 e utilizar no projeto com a variavél de ambiente `FIREBASE_CREDENTIALS` ou definir no `application.properties`.

7. **Compilar o projeto**:
    ```sh
    mvn clean install
    ```

8. **Executar o projeto**:
    ```sh
    mvn quarkus:dev
    ```

## Endpoints
- **POST /documento**: Endpoint para manipulação de documentos.
    - **Parâmetros**:
        - `tipo`: Tipo do documento (ex: `atestadoMatricula`, `historico`, `declaracaoVinculo` e `historicoEmentas`).
        - `senha`: Senha do SIGAA
        - `header Authorization`: Token de autenticação do Firebase.
      
- **POST /sync**: Endpoint para sincronização de alunos.
    - **Parâmetros**:
        - `senha`: Senha do SIGAA
        - `header Authorization`: Token de autenticação do Firebase.
         
- **POST /noticias**: Endpoint para obter notícias.
    - **Parâmetros**:
        - `limit`: Limite de notícias a serem retornadas.
        - `filter`: Filtro de notícias (ex: `ifrs`, `campus`, `ensino`, `pesquisa`, `extensao`).
- **GET /noticias**: Endpoint para obter notícias.
- **POST /editais**: Endpoint para obter editais.
    - **Parâmetros**:
        - `limit`: Limite de editais a serem retornadas.
        - `filter`: Filtro de editais (ex: `ifrs`, `campus`, `ensino`, `pesquisa`, `extensao`).
- **GET /editais**: Endpoint para obter editais.

### Exemplo de Uso
```sh
# Obter atestado de matrícula
curl -X POST http://localhost:8080/documento -H "Authorization: Bearer <token>" -d "{"tipo":"atestadoMatricula","senha":"giropops"}"

# Sinconizar aluno
curl -X POST http://localhost:8080/sync -H "Authorization: Bearer <token>" -d "{"senha":"giropops"}"

# Obter notícias
curl -X POST http://localhost:8080/noticias

# Obter notícias com filtro
curl -X POST http://localhost:8080/noticias -d "{"filter":"ifrs"}"

# Obter editais
curl -X POST http://localhost:8080/editais

# Obter notícias com filtro
curl -X POST http://localhost:8080/noticias -d "{"filter":"ifrs"}"

```

## Documentação javadoc
Casos deseje gerar a documentação javadoc, execute um dos comandos abaixo:
```sh
mvn javadoc:javadoc
```
```sh
./mvnw javadoc:javadoc
```


## Documentação
- **Documentação da API**: [https://app.poa.ifrs.edu.br/meuifpoa/q/swagger-ui/](https://app.poa.ifrs.edu.br/meuifpoa/q/swagger-ui/)
- **Documentação do Firebase**: [https://firebase.google.com/docs](https://firebase.google.com/docs)
- **Documentação do Quarkus**: [https://quarkus.io/guides](https://quarkus.io/guides)
- **Documentação do JSoup**: [https://jsoup.org/cookbook/](https://jsoup.org/cookbook/)
- **Documentação do OkHttp**: [https://square.github.io/okhttp/](https://square.github.io/okhttp/)
- **Documentação local do projeto**: [docs/](docs/)

## Licença
Este projeto está licenciado sob a Licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

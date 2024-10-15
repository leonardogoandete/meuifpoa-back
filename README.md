# Projeto Backend

## Descrição
Este projeto é um serviço backend desenvolvido em Java utilizando o framework Quarkus. Ele inclui funcionalidades para manipulação de documentos e integração com serviços externos.
Serve como api para obter informações de documentos, notícias e notas de alunos e acessar essas informações através de endpoints.

## Tecnologias Utilizadas
- **Java**
- **Quarkus**
- **Maven**

## Estrutura do Projeto
### Diretórios
- `src/main/java/br/com/ifrs/meuifpoaback/service/DocumentoService.java`: Contém a lógica de manipulação de documentos.
- `src/main/java/br/com/ifrs/meuifpoaback/controller/DocumentoController.java`: Contém os endpoints para manipulação de documentos.
- `src/main/java/br/com/ifrs/meuifpoaback/model/DocumentoRequest.java`: Contém a definição do modelo de documento.
- `src/main/java/br/com/ifrs/meuifpoaback/service/NoticiaService.java`: Contém a lógica de notícias.
- `src/main/java/br/com/ifrs/meuifpoaback/controller/NoticiaController.java`: Contém os endpoints para manipulação de notícias.
- `src/main/java/br/com/ifrs/meuifpoaback/model/Noticia.java`: Contém a definição do modelo de notícia.
- `src/main/java/br/com/ifrs/meuifpoaback/service/SyncService.java`: Contém a lógica de sincronização de alunos.
- `src/main/java/br/com/ifrs/meuifpoaback/controller/SyncController.java`: Contém os endpoints para sincronização de alunos.
- `src/main/java/br/com/ifrs/meuifpoaback/model/Perfil.java`: Contém a definição do modelo de aluno.
- `src/main/java/br/com/ifrs/meuifpoaback/model/Notas.java`: Contém a definição do modelo de notas.
- `src/main/java/br/com/ifrs/meuifpoaback/configuration/FirebaseInitializer.java`: Contém a configuração do Firebase.
- `src/main/java/br/com/ifrs/meuifpoaback/configuration/FirebaseSecurityContext.java`: Contém a configuração do contexto de segurança do Firebase.
- `src/main/java/br/com/ifrs/meuifpoaback/configuration/FirebaseTokenFilter.java`: Contém o filtro de token do Firebase.
- `src/main/java/br/com/ifrs/meuifpoaback/utils/FirestoreUtils.java`: Contém métodos utilitários para manipulação do Firestore.


## Configuração do Ambiente
1. **Pré-requisitos**:
    - JDK 11 ou superior
    - Maven 3.6 ou superior

2. **Clonar o repositório**:
    ```sh
    git clone https://github.com/leonardogoandete/projeto-backend.git
    cd projeto-meuifpoaback
    ```
3. **Configurar certificado para autenticação dos usuarios com o Firebase**:
    - Configurar o arquivo `application.properties` com as informações do Firebase adicionando as seguintes linhas:
    ```
    quarkus.smallrye-jwt.enabled=true
    smallrye.jwt.verify.key.location=https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com
    mp.jwt.verify.issuer=https://securetoken.google.com/<ID_PROJETO>
    ```

4. **Compilar o projeto**:
    ```sh
    mvn clean install
    ```

5. **Executar o projeto**:
    ```sh
    mvn spring-boot:run
    ```

## Endpoints
- **POST /documento**: Endpoint para manipulação de documentos.
    - **Parâmetros**:
        - `tipo`: Tipo do documento (ex: `atestadoMatricula`, `historico`, `declaracaoVinculo` e `historicoEmentas`).
        - `senha`: Senha do SIGAA
        - `header Authorization`: Token de autenticação do Firebase.
      
- **POST /sincronizar**: Endpoint para sincronização de alunos.
    - **Parâmetros**:
        - `senha`: Senha do SIGAA
        - `header Authorization`: Token de autenticação do Firebase.
         
- **POST /noticia**: Endpoint para obter notícias.
    - **Parâmetros**:
        - `limit`: Limite de notícias a serem retornadas.
        - `filter`: Filtro de notícias (ex: `ifrs`, `campus`, `ensino`, `pesquisa`, `extensao`).

### Exemplo de Uso
```sh
# Obter atestado de matrícula
curl -X POST http://localhost:8080/documento -H "Authorization: Bearer <token>" -d "{"tipo":"atestadoMatricula","senha":"giropops"}"

# Sinconizar aluno
curl -X POST http://localhost:8080/sincronizar -H "Authorization: Bearer <token>" -d "{"senha":"giropops"}"

# Obter notícias
curl -X POST http://localhost:8080/noticias
```

## Licença
Este projeto está licenciado sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
```

Adapte as seções conforme necessário para refletir as especificidades do seu projeto.
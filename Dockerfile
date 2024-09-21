# Etapa 1: Build da aplicação
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw /app/mvnw
COPY pom.xml /app/pom.xml

# Baixa as dependências para a compilação
RUN ./mvnw dependency:go-offline

COPY ./src/main/ ./src/main/

# Empacota a aplicação como um Uber Jar
RUN ./mvnw package -Dquarkus.package.type=uber-jar

# Etapa 2: Configuração do ambiente de execução (runtime)
FROM eclipse-temurin:21-jre-alpine AS app

# Instalar dependências necessárias para Playwright e Node.js
RUN apk add --no-cache \
    bash \
    nss \
    freetype \
    harfbuzz \
    nodejs \
    npm \
    chromium \
    firefox \
    ffmpeg

# Instalar Playwright e navegadores como root
RUN npm install -g playwright && \
    npx playwright install

RUN chmod +x /tmp/node
## Adiciona um usuário não-root
#RUN addgroup -S appgroup && \
#    adduser -S appuser -G appgroup
#
## Mudar para o usuário não-root
#USER appuser

# Copia o Jar gerado na etapa de build
COPY --from=build /app/target/*.jar /app/app.jar

# Expor a porta 8080
EXPOSE 8080

# Comando de entrada para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

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
RUN ./mvnw package -Dquarkus.profile=prod -Dquarkus.package.type=uber-jar

FROM eclipse-temurin:21-jre-alpine AS app
#Trocar timezone
RUN apk add --no-cache tzdata
ENV TZ=America/Sao_Paulo
RUN addgroup -S appgroup && \
    adduser -S appuser -G appgroup
USER appuser
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
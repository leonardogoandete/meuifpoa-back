FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw /app/mvnw
COPY pom.xml /app/pom.xml

RUN ./mvnw dependency:go-offline

COPY ./src/main/ ./src/main/

RUN ./mvnw package -Dquarkus.package.type=uber-jar

FROM eclipse-temurin:21-jre-alpine AS app
RUN addgroup -S appgroup && \
    adduser -S appuser -G appgroup
USER appuser
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

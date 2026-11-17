# Etapa de Build (Compilação)
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
# Baixa as dependências primeiro para aproveitar o cache de camadas do Docker
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
# Compila o projeto e gera o .jar, ignorando os testes para ser mais rápido
RUN mvn -B clean package -DskipTests

# Etapa de Execução (Runtime)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

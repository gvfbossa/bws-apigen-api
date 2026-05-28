# === BUILD ===
FROM maven:3.9.2-eclipse-temurin-21 AS build
WORKDIR /app

# Copia pom e baixa dependências (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o resto do projeto e faz build
COPY src ./src
RUN mvn clean package -DskipTests

# === RUNTIME ===
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copia o JAR da build
COPY --from=build /app/target/bws-apigen-0.0.1-SNAPSHOT.jar app.jar

# Variáveis de ambiente (podem ser sobrescritas pelo docker-compose)
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS=""

# Expõe a porta da API
EXPOSE 8080

# Entrypoint
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
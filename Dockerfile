# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

# ---------- Run stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Render sets PORT; Spring should read it via server.port=${PORT:8080}
EXPOSE 8080

COPY --from=build /app/target/*.jar app.jar
CMD ["sh", "-c", "java -jar app.jar"]

# Paso 1: Compilar la aplicación usando Maven con Java 25 de Amazon Corretto
FROM amazoncorretto:25-alpine AS build
WORKDIR /app

# Instalar Maven de forma limpia sin la opción errónea
RUN apk add --no-cache maven

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Paso 2: Ejecutar la aplicación usando la versión ligera de Java 25
FROM openjdk:25-ea-14-jdk-slim
WORKDIR /app
COPY --from=build /app/target/shipment-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
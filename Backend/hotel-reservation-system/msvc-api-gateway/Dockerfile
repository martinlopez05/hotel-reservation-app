FROM openjdk:17-jdk-alpine AS builder

WORKDIR /app/msvc-api-gateway

COPY ./pom.xml /app
COPY ./msvc-api-gateway/.mvn ./.mvn
COPY ./msvc-api-gateway/mvnw .
COPY ./msvc-api-gateway/pom.xml .


RUN chmod +x mvnw

COPY ./msvc-api-gateway/src ./src

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app


COPY --from=builder /app/msvc-api-gateway/target/msvc-api-gateway-0.0.1-SNAPSHOT.jar .


EXPOSE 8090

ENTRYPOINT ["java", "-jar", "msvc-api-gateway-0.0.1-SNAPSHOT.jar"]
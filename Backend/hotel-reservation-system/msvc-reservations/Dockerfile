FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app/msvc-reservations

COPY ./pom.xml /app
COPY ./msvc-reservations/.mvn ./.mvn
COPY ./msvc-reservations/mvnw .
COPY ./msvc-reservations/pom.xml .


RUN chmod +x mvnw

COPY ./msvc-reservations/src ./src


RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app


COPY --from=builder /app/msvc-reservations/target/msvc-reservations-0.0.1-SNAPSHOT.jar .


EXPOSE 8003

ENTRYPOINT ["java", "-jar", "msvc-reservations-0.0.1-SNAPSHOT.jar"]
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app/msvc-users


COPY ./pom.xml /app


COPY ./msvc-users/.mvn ./.mvn
COPY ./msvc-users/mvnw .
COPY ./msvc-users/pom.xml .
RUN chmod +x mvnw
COPY ./msvc-users/src ./src


RUN ./mvnw clean package -DskipTests


FROM openjdk:17-jdk-alpine
WORKDIR /app


COPY --from=builder /app/msvc-users/target/msvc-users-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8004
ENTRYPOINT ["java", "-jar", "app.jar"]

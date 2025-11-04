FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app/msvc-reviews

COPY ./pom.xml /app
COPY ./msvc-reviews/.mvn ./.mvn
COPY ./msvc-reviews/mvnw .
COPY ./msvc-reviews/pom.xml .


RUN chmod +x mvnw

COPY ./msvc-reviews/src ./src


RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app


COPY --from=builder /app/msvc-reviews/target/msvc-reviews-0.0.1-SNAPSHOT.jar .


EXPOSE 8006

ENTRYPOINT ["java", "-jar", "msvc-reviews-0.0.1-SNAPSHOT.jar"]
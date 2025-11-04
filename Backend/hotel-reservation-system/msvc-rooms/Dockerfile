FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app/msvc-rooms

COPY ./pom.xml /app
COPY ./msvc-rooms/.mvn ./.mvn
COPY ./msvc-rooms/mvnw .
COPY ./msvc-rooms/pom.xml .


RUN chmod +x mvnw

COPY ./msvc-rooms/src ./src


RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jdk-alpine

WORKDIR /app


COPY --from=builder /app/msvc-rooms/target/msvc-rooms-0.0.1-SNAPSHOT.jar .


EXPOSE 8001

ENTRYPOINT ["java", "-jar", "msvc-rooms-0.0.1-SNAPSHOT.jar"]
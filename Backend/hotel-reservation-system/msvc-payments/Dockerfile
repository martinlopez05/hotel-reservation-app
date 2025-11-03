FROM openjdk:17-jdk-alpine AS builder

WORKDIR /app/msvc-payments

COPY ./pom.xml /app
COPY ./msvc-payments/.mvn ./.mvn
COPY ./msvc-payments/mvnw .
COPY ./msvc-payments/pom.xml .


RUN chmod +x mvnw

COPY ./msvc-payments/src ./src


RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jdk-alpine

WORKDIR /app


COPY --from=builder /app/msvc-payments/target/msvc-payments-0.0.1-SNAPSHOT.jar .


EXPOSE 8005

ENTRYPOINT ["java", "-jar", "msvc-payments-0.0.1-SNAPSHOT.jar"]
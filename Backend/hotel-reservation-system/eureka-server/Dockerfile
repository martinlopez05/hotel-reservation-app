FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app/eureka-server

COPY ./pom.xml /app
COPY ./eureka-server/.mvn ./.mvn
COPY ./eureka-server/mvnw .
COPY ./eureka-server/pom.xml .


RUN chmod +x mvnw

COPY ./eureka-server/src ./src


RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jdk-alpine

WORKDIR /app


COPY --from=builder /app/eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar .


EXPOSE 8761

ENTRYPOINT ["java", "-jar", "eureka-server-0.0.1-SNAPSHOT.jar"]
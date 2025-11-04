FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app/msvc-hotels

COPY ./pom.xml /app
COPY ./msvc-hotels/.mvn ./.mvn
COPY ./msvc-hotels/mvnw .
COPY ./msvc-hotels/pom.xml .


RUN chmod +x mvnw

COPY ./msvc-hotels/src ./src


RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jdk-alpine

WORKDIR /app


COPY --from=builder /app/msvc-hotels/target/msvc-hotels-0.0.1-SNAPSHOT.jar .


EXPOSE 8002

ENTRYPOINT ["java", "-jar", "msvc-hotels-0.0.1-SNAPSHOT.jar"]
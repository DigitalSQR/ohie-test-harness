# Stage 1: Build application
FROM maven:3.9.2-amazoncorretto-17 AS builder

WORKDIR /app
COPY . /app
RUN mvn clean install -Dmaven.test.skip=true

# Stage 2: Run application
FROM eclipse-temurin:17-jre-jammy

ARG PROFILE;

RUN mkdir /app
COPY --from=builder /app/target/testing-harness-tool-0.0.1-SNAPSHOT.jar /app/app.jar
RUN sh -c 'touch /app/app.jar'

CMD ["java", "-jar", "/app/app.jar", "--spring.profiles.active=$PROFILE"]

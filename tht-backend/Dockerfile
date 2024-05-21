# Stage 1: Build application
FROM maven:3.9.2-amazoncorretto-17 AS builder

WORKDIR /app
COPY . /app
RUN mvn clean install -Dmaven.test.skip=true

# Stage 2: Run application
FROM eclipse-temurin:17-jre-jammy

# Install netcat for wait-for-it.sh
#RUN apt-get update && apt-get install -y netcat

# Copy wait-for-it.sh script

#COPY wait-for-it.sh /wait-for-it.sh
#RUN chmod +x /wait-for-it.sh

ARG PROFILE;



RUN mkdir /app
COPY --from=builder /app/target/testing-harness-tool-0.0.1-SNAPSHOT.jar /app/app.jar
RUN sh -c 'touch /app/app.jar'
#CMD ["/wait-for-it.sh", "gitb-ui:9000", "--strict", "--", "java", "-jar", "/app/app.jar"]
CMD ["java", "-jar", "/app/app.jar", "--spring.profiles.active=$PROFILE"]

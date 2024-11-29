# start with base image containing Java runtime
FROM openjdk:21-jdk-slim

# Install curl
RUN apt-get update && apt-get install -y curl

MAINTAINER jstasko
#Copy to docker file system
COPY target/e-commerce-0.0.1-SNAPSHOT.jar e-commerce-0.0.1-SNAPSHOT.jar
#Execute docker
ENTRYPOINT ["java", "-jar", "e-commerce-0.0.1-SNAPSHOT.jar"]
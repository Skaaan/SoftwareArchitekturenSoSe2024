FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/service-discovery-0.0.1-SNAPSHOT.jar service-discovery.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "service-discovery.jar"]

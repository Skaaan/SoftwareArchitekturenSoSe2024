FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/service-discovery-1.0-SNAPSHOT.jar service-discovery.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "service-discovery.jar"]
# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY target/service-discovery-0.0.1-SNAPSHOT.jar service-discovery.jar

# Make port 8761 available to the world outside this container
EXPOSE 8761

# Run the application
ENTRYPOINT ["java", "-jar", "service-discovery.jar"]

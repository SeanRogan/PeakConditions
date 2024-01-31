# Stage 1: Build the application using a Maven Docker image
FROM maven:3.8.4-openjdk-17 as build
WORKDIR /app
#Copy over required data
COPY pom.xml .
COPY src /app/src
# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create the final Docker image with just the JAR file
FROM openjdk:17-alpine
WORKDIR /app
# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Run the JAR file
ENTRYPOINT ["java","-jar","app.jar", "--spring.profiles.active=${SPRING_ACTIVE_PROFILE}"]
# Use Gradle image to build the project
FROM gradle:8.5-jdk17 AS builder

# Set working directory
WORKDIR /app

# Copy Gradle project files
COPY . .

# Build the fat jar (or normal jar, depending on your build script)
RUN gradle shadowJar --no-daemon

# Use slim JDK image for running the app
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the jar from the builder stage
COPY --from=builder /app/build/libs/Lab-Parallele-all.jar app.jar

# Expose the app port
EXPOSE 8081

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]

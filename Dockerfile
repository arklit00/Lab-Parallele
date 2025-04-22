# Use official Kotlin JDK image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy build/libs JAR (adjust if your output path or name differs)
COPY build/libs/Lab-Parallele-all.jar

# Expose port your app listens on
EXPOSE 8081

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]

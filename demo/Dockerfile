# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-oracle

# Set the working directory inside the container
# WORKDIR /app

# Copy the packaged JAR file into the container at the defined working directory
ADD target/demo-0.0.1-SNAPSHOT.jar product-service.jar

# Expose the port the application will run on
EXPOSE 8080

# Define environment variables (if necessary)
# give it explicitly while running the container
# ENV SPRING_DATA_MONGODB_URI mongodb://localhost:27017/comdemo

# Run the application when the container starts
CMD ["java", "-jar", "product-service.jar"]

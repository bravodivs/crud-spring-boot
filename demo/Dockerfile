# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-oracle

# Copy the packaged JAR file into the container at the defined working directory
ADD target/product-service-1.0.0.jar product-service.jar

# Expose the port the application will run on
EXPOSE 8080

# Run the application when the container starts
CMD ["java", "-jar", "product-service.jar"]

# Get the gradle docker image
FROM mcr.microsoft.com/java/jdk:14-zulu-ubuntu

# Set the working directory
WORKDIR /xenus

# Copy source files
COPY . .

# Set ENV
ENV PORT=8080

# Expose the port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "build/libs/Xenus.jar"]
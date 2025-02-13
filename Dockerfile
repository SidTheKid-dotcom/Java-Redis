# Use OpenJDK as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /src

# Copy all Java source files to the working directory
COPY . /src/

# Compile all Java files
RUN javac *.java

# Expose port 8100 for communication
EXPOSE 8100

# Command to run the server when the container starts
CMD ["java", "MulticlientServer"]

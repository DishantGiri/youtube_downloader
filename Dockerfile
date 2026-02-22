# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Install Python and pip
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    && ln -s /usr/bin/python3 /usr/bin/python \
    && rm -rf /var/lib/apt/lists/*

# Install Python dependencies
# Using pytubefix as it's more stable for currently working YouTube downloads
RUN pip3 install pytubefix

# Copy the built jar from the build stage
COPY --from=build /app/target/youtube-downloader-0.0.1-SNAPSHOT.jar app.jar

# Copy the Python script (it's in src/main/python in the build stage, but needs to be accessible at runtime)
# The Java code expects it at: "src/main/python/downloader.py"
COPY src/main/python/downloader.py src/main/python/downloader.py

# Create downloads directory
RUN mkdir downloads

# Expose the application port
EXPOSE 8070

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

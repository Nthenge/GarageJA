
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src/ main/
RUN chmod +x mvnw
RUN ./mvnw package -DskipTests
# Extract the JAR file
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Stage 2: Create the final lightweight runtime image (using a Java Runtime Environment)
FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
ARG JAR_FILE=/app/target/*.jar
COPY --from=build ${JAR_FILE} app.jar
# Set the port Spring Boot should listen on (Render uses $PORT)
ENV PORT 8083
EXPOSE ${PORT}
# The command to run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
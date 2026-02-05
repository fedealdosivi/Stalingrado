FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY dev/src ./dev/src
COPY src ./src

# Build with Spring Boot repackage (configured in pom.xml)
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the Spring Boot executable JAR
COPY --from=build /app/target/stalingrado-battle-simulator-2.0.0.jar app.jar

EXPOSE 8080
ENV PORT=8080

CMD ["java", "-jar", "app.jar"]

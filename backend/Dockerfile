FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY backend/pom.xml ./pom.xml
COPY backend/src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/sauto-api-1.0.0.jar ./sauto-api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/sauto-api.jar"]

FROM maven:3.9.9-amazoncorretto-17 AS build
COPY database /app/database
COPY src /app/src
COPY pom.xml /app

WORKDIR /app
RUN mvn clean install

FROM eclipse-temurin:17

COPY --from=build /app/target/bands-api-0.0.1-SNAPSHOT.jar /app/app.jar

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

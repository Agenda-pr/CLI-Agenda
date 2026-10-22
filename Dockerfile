FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests dependency:copy-dependencies -DincludeScope=runtime


FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/CLI-Agenda-1.0-SNAPSHOT.jar app.jar
COPY --from=build /app/target/dependency ./lib
ENTRYPOINT ["java", "-cp", "app.jar:lib/*", "Main"]

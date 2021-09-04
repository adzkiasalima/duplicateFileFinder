FROM maven:3.6.0-jdk-11-slim AS build
COPY . /app
WORKDIR /app
RUN mvn -f /app/pom.xml clean install

FROM openjdk:11-jre-slim

COPY --from=build /app/target/dropsuitetest-1.0.jar /app/dropsuite.jar
WORKDIR /app

ENTRYPOINT ["java", "-jar", "dropsuite.jar"]

CMD ["tail", "-f", "/dev/null"]
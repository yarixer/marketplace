# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw -q -Dmaven.test.skip=true dependency:go-offline

COPY src ./src
RUN ./mvnw -q -Dmaven.test.skip=true package

FROM eclipse-temurin:21-jre
WORKDIR /application

RUN useradd --create-home --uid 10001 appuser

COPY --from=build /workspace/target/*.jar application.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC -Dfile.encoding=UTF-8"

EXPOSE 8080

USER 10001

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /application/application.jar"]
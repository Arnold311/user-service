FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar /user-service.jar.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar", "user-service.jar"]


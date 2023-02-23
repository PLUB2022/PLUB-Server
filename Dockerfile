
FROM openjdk:17-jdk
ENV APP_HOME=/home/app/
WORKDIR $APP_HOME
COPY build/libs/*.jar plub-server.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=secret-prod", "-jar", "plub-server.jar"]

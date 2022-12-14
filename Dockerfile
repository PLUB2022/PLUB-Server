
FROM openjdk:17-jdk
ENV APP_HOME=/home/app/
WORKDIR $APP_HOME
COPY build/libs/*.jar plub-server.jar
RUN mkdir -p temp
EXPOSE 8080
ENTRYPOINT ["java","-jar","plub-server.jar"]

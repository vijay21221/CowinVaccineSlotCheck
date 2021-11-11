FROM openjdk:8-jdk-alpine
ARG JAR_FILE=/build/libs/demo-0.1-all.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
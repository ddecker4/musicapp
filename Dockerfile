FROM eclipse-temurin:21-jdk-alpine-3.22
ARG JAR_FILE=musicapp-backend/build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
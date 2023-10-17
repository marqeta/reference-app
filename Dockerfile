FROM openjdk:17-jdk-slim
COPY build/libs/referenceapp-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

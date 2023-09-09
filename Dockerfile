FROM eclipse-temurin:17.0.8_7-jre-alpine

COPY build/libs/*.jar /opt/app/application.jar

RUN addgroup -S spring && adduser -S spring -G spring

USER spring:spring

CMD java -jar /opt/app/application.jar
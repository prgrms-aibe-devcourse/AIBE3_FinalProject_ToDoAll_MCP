FROM eclipse-temurin:21-jdk-alpine


WORKDIR /app

COPY build/libs/*.jar app.jar

ENV TZ=Asia/Seoul

ENTRYPOINT ["java", "-jar", "app.jar"]
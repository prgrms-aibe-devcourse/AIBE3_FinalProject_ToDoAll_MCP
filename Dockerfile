FROM eclipse-temurin:21-jdk-alpine


WORKDIR /app

COPY build/libs/mcp-0.0.1-SNAPSHOT.jar app.jar

ENV TZ=Asia/Seoul

ENTRYPOINT ["java", "-jar", "app.jar"]
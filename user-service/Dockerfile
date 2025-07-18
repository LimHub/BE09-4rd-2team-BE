FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY build/libs/*.jar user-service.jar

RUN chmod +x user-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "user-service.jar"]

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY build/libs/*.jar app.jar

RUN chmod +x app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]

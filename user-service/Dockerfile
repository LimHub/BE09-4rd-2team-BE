FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY build/libs/user-service-0.0.1-SNAPSHOT.jar app.jar

RUN chmod +x app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

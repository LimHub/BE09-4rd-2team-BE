FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY build/libs/discovery-service-0.0.1-SNAPSHOT.jar app.jar

RUN chmod +x app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY build/libs/gateway-0.0.1-SNAPSHOT.jar app.jar

RUN chmod +x app.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]

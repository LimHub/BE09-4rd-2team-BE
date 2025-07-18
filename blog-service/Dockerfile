FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY build/libs/*.jar blog-service.jar

RUN chmod +x blog-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "blog-service.jar"]
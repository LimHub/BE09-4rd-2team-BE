FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY build/libs/*.jar discovery.jar
RUN chmod +x discovery.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "discovery.jar"]

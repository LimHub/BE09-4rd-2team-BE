FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY build/libs/*.jar gateway.jar
RUN chmod +x gateway.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "gateway.jar"]

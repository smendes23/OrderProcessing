FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/OrderProcessing-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
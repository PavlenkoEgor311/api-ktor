FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY build/libs/com.example.ktor-api-all.jar /app/com.example.ktor-api-all.jar
EXPOSE 8080
CMD ["java", "-jar", "com.example.ktor-api-all.jar"]
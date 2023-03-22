# FROM openjdk:11-jdk-slim
#
# WORKDIR /src
# COPY . /src
#
# RUN apt-get update
# RUN apt-get install -y dos2unix
# RUN dos2unix gradlew
#
# RUN bash gradlew shadow
#
# WORKDIR /run
# RUN cp /src/build/libs/com.example.ktor-api-all.jar /run/server.jar
#
# EXPOSE 8081
#
# CMD java -jar /run/server.jar


# FROM openjdk:11-jdk-slim
# WORKDIR .
# COPY ./build/libs/com.example.ktor-api-all.jar /app.jar
# CMD ["java", "-jar", "/app.jar"]

# FROM openjdk:11-jdk-slim AS build
# COPY --chown=gradle:gradle . /home/gradle/src
# WORKDIR /home/gradle/src
# RUN gradle buildFatJar --no-daemon
#
# FROM openjdk:11
# EXPOSE 8080:8081
# RUN mkdir /ktor-api
# COPY --from=build ./build/libs/com.example.ktor-api-all.jar /app.jar
# ENTRYPOINT ["java","-jar","/app/ktor-docker-sample.jar"]
FROM openjdk:11-jre-slim-buster
COPY ./build/libs/com.example.ktor-api-all.jar /app/my-api.jar
EXPOSE 8081
CMD ["java", "-jar", "/app/my-api.jar"]
# Use the official gradle image to create a build artifact.
# FROM gradle:7.5.1 as builder
#
# # Copy local code to the container image.
# COPY build.gradle.kts .
# COPY gradle.properties .
# COPY src ./src
#
# # Build a release artifact.
# RUN gradle installDist
#
# FROM openjdk:11-jdk-slim
# EXPOSE 8080:8081
# RUN mkdir /app
# COPY --from=builder /home/gradle/build/install/gradle /app/
# WORKDIR /app/bin
# CMD ["./gradle"]
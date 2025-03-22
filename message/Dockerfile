FROM gradle:7.6-jdk17 as builder

WORKDIR /app

COPY common /app/common/

WORKDIR /app/common
RUN gradle clean build publishToMavenLocal -x test

WORKDIR /app
COPY message /app/message/
WORKDIR /app/message
RUN gradle clean bootJar -x test

FROM eclipse-temurin:17-jre
COPY --from=builder /app/message/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
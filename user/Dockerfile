FROM gradle:7.6-jdk17 as builder

WORKDIR /app

# 먼저 common 모듈 복사 및 빌드
COPY common /app/common/
WORKDIR /app/common
RUN gradle clean build publishToMavenLocal -x test

# user 모듈 복사 및 빌드
WORKDIR /app
COPY user /app/user/
# 변경: 내부의 'user' 폴더로 진입
WORKDIR /app/user
RUN gradle clean bootJar -x test

FROM eclipse-temurin:17-jre
COPY --from=builder /app/user/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

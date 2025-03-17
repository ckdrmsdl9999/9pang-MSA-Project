FROM gradle:7.6-jdk17 as builder

WORKDIR /app

# 먼저 common 모듈 복사 및 빌드
COPY common /app/common/
WORKDIR /app/common
RUN gradle clean build publishToMavenLocal -x test

# company 모듈 복사 및 빌드
WORKDIR /app
COPY company /app/company/
# 변경: 내부의 'company' 폴더로 진입
WORKDIR /app/company
RUN gradle clean bootJar -x test

FROM eclipse-temurin:17-jre
COPY --from=builder /app/company/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

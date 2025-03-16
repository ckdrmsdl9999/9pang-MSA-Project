FROM gradle:7.6-jdk17 as builder

# 작업 디렉토리 설정
WORKDIR /app

# 먼저 common 모듈을 복사 (이제 빌드 컨텍스트가 상위 디렉토리이므로 직접 접근 가능)
COPY common /app/common/

# common 모듈 빌드 및 로컬 Maven 저장소에 발행
WORKDIR /app/common
RUN gradle clean build publishToMavenLocal -x test

# order 모듈 복사 및 빌드
WORKDIR /app
COPY gateway /app/gateway/
WORKDIR /app/gateway
RUN gradle clean bootJar -x test

# 최종 실행 이미지 설정
FROM eclipse-temurin:17-jre
COPY --from=builder /app/gateway/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
# OpenJDK 17 기반의 슬림 이미지 사용
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 산출물 JAR 파일 복사
ARG JAR_FILE=build/libs/inquiryingAccident-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 환경 변수 설정 (서울 시간대)
ENV TZ=Asia/Seoul

# 컨테이너 노출 포트
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

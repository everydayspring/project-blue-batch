# 환경구성
FROM azul/zulu-openjdk:17-latest

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} application.jar

ENTRYPOINT ["java", "-jar", "/application.jar"]
FROM azul/zulu-openjdk-alpine:17-latest
EXPOSE 8080

ENV PROFILE=remote
ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT java -jar /app.jar --spring.profiles.active=${PROFILE}
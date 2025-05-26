FROM azul/zulu-openjdk-alpine:21-latest
EXPOSE 8080

ENV PROFILE=remote

COPY *.jar app.jar

ENTRYPOINT java -jar /app.jar --spring.profiles.active=${PROFILE}
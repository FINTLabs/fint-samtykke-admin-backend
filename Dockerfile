FROM gradle:8.1.1-jdk17 as builder
USER root
COPY . .
RUN gradle --no-daemon build

FROM eclipse-temurin:17-jre
ENV JAVA_TOOL_OPTIONS -XX:+ExitOnOutOfMemoryError
COPY --from=builder /home/gradle/build/libs/fint-samtykke-admin-backend*.jar /data/app.jar
ENTRYPOINT ["java", "-jar", "/data/app.jar"]

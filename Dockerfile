FROM openjdk:17-jdk-slim AS base
WORKDIR /app
COPY build/libs/life4cut-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "life4cut-0.0.1-SNAPSHOT.jar"]

FROM base as dev-api
ENV SPRING_PROFILES_ACTIVE dev

FROM base as prod-api
ENV SPRING_PROFILES_ACTIVE prod
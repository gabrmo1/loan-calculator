FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
#COPY settings.gradle .
COPY src src
RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
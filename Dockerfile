# Мулти-стейдж билд за по-малък образ
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Копиране на pom.xml и изтегляне на зависимостите
COPY pom.xml .
RUN mvn dependency:go-offline

# Копиране на сорс кода
COPY src ./src

# Компилиране и пакетиране (използваме Maven, защото нямаме mvnw)
RUN mvn clean package -DskipTests

# Финален образ - използваме Java 17, въпреки че имаш Java 23 локално
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Копиране на JAR файла от build стейджа
COPY --from=build /app/target/bank-wallet-rest-api-0.0.1-SNAPSHOT.jar app.jar

# Създаване на non-root user за сигурност
RUN addgroup -g 1000 -S spring && \
    adduser -u 1000 -S spring -G spring
USER spring

# Експорт на порта (Spring Boot default е 8080)
EXPOSE 8080

# Стартиране на приложението
ENTRYPOINT ["java", "-jar", "app.jar"]
# Используем официальный образ OpenJDK как базовый образ
FROM openjdk:17-jdk-slim as build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем build.gradle и скачиваем зависимости
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon

# Копируем исходный код и строим приложение
COPY src /app/src
RUN gradle build -x test --no-daemon

# Используем более легкий образ для финального контейнера
FROM openjdk:17-jdk-slim

# Копируем собранный jar файл из предыдущего этапа
COPY --from=build /app/build/libs/spring-boot-app.jar /spring-boot-app.jar

# Открываем порт для приложения
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "/spring-boot-app.jar"]

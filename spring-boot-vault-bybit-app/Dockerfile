# Используем официальный образ OpenJDK как базовый образ
FROM openjdk:17-ea-slim AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем build.gradle (и только его, если у вас нет settings.gradle)
COPY build.gradle ./

# Качаем зависимости (для кэширования зависимостей)
RUN gradle dependencies --no-daemon

# Если у вас есть settings.gradle, добавьте его, иначе просто копируем исходный код
# COPY settings.gradle ./

# Копируем весь код проекта
COPY src /app/src

# Собираем приложение
RUN gradle build -x test --no-daemon

# Используем более легкий образ для финального контейнера
FROM openjdk:17-ea-slim

# Копируем собранный jar файл из предыдущего этапа
COPY --from=build /app/build/libs/spring-boot-app.jar /spring-boot-app.jar

# Открываем порт для приложения
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "/spring-boot-app.jar"]

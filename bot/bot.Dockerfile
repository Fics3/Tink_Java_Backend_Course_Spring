FROM openjdk:21
LABEL authors="digital-nishtyak"

ENV TELEGRAM_API_KEY=$TELEGRAM_API_KEY

COPY target/bot.jar app/bot.jar

WORKDIR /app

CMD ["java", "-jar", "bot.jar"]

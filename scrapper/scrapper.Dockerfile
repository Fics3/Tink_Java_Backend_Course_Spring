FROM openjdk:21
LABEL authors="digital-nishtyak"

COPY target/scrapper.jar app/scrapper.jar

WORKDIR /app

CMD ["java", "-jar", "scrapper.jar"]

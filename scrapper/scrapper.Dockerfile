FROM maven:3.9.6 AS build
WORKDIR /app
#Root
COPY pom.xml .

#Bot
COPY /bot/src ./bot/src
COPY /bot/pom.xml ./bot/pom.xml

#DTO's
COPY /dto/src ./dto/src
COPY /dto/pom.xml ./dto/pom.xml

#Scrapper
COPY /scrapper/src ./scrapper/src
COPY /scrapper/pom.xml ./scrapper/pom.xml

#Scrapper-jooq
COPY /scrapper-jooq/src ./scrapper-jooq/src
COPY /scrapper-jooq/pom.xml ./scrapper-jooq/pom.xml

RUN mvn -pl scrapper -am package -Dmaven.test.skip=true

# Stage 2: Final stage
FROM openjdk:21
LABEL authors="digital-nishtyak"

ENV BOT_URL=${BOT_URL} \
    KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}\
    SERVER_PORT=${SERVER_PORT} \
    MANAGEMENT_PORT=${MANAGEMENT_PORT}

COPY --from=build /app/scrapper/target/scrapper.jar scrapper.jar

ENTRYPOINT ["java","-jar","/scrapper.jar"]

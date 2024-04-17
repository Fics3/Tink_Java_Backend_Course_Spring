FROM openjdk:21
LABEL authors="digital-nishtyak"

ENV BOT_URL=${BOT_URL} \
    KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}\
    SERVER_PORT=${SERVER_PORT} \
    MANAGEMENT_PORT=${MANAGEMENT_PORT}


COPY ./target/scrapper.jar scrapper.jar

ENTRYPOINT ["java","-jar","/scrapper.jar"]

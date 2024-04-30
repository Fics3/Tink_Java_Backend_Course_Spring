package edu.java.bot.service.kafka;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class KafkaIntegrationTest {
    protected static KafkaContainer KAFKA;

    static {
        KAFKA = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest")
        );
        KAFKA.start();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("kafka.enable", () -> true);
        registry.add("KAFKA_BOOTSTRAP_SERVERS", KAFKA::getBootstrapServers);
    }
}

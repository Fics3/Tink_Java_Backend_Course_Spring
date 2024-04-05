package edu.java.bot.configuration;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty
    String telegramToken,
    @NestedConfigurationProperty
    KafkaProperties kafkaProperties
) {

    public record KafkaProperties(Topic topic, DlqTopic dlqTopic) {
        public record Topic(String name, Integer partitionsNum, Integer replicasNum) {
        }

        public record DlqTopic(String name, Integer partitionsNum, Integer replicasNum) {
        }
    }
}

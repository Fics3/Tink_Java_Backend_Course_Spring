package edu.java.bot.configuration;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty
    String telegramToken,
    @NestedConfigurationProperty
    @NotNull
    @Bean
    ScrapperProperties scrapperProperties,
    @NotNull
    @NestedConfigurationProperty
    RetryProperties retryProperties,
    @NotNull
    @NestedConfigurationProperty
    RateLimitProperty rateLimitProperty,
    @NotNull
    @NestedConfigurationProperty
    KafkaProperties kafkaProperties
) {
    public record ScrapperProperties(String chat, String links, String tgChatId, String url) {
    }

    public record RetryProperties(Integer attempts,
                                  Duration backoff,
                                  BackoffStrategy backoffStrategy,
                                  List<Integer> retryStatusCodes) {
        public enum BackoffStrategy {
            constant,
            linear,
            exponent

        }
    }

    public record RateLimitProperty(Integer limit, Integer refillLimit, Duration delayRefill) {
    }

    public record KafkaProperties(Topic topic, DlqTopic dlqTopic) {
        public record Topic(String name, Integer partitionsNum, Integer replicasNum) {
        }

        public record DlqTopic(String name, Integer partitionsNum, Integer replicasNum) {
        }
    }
}

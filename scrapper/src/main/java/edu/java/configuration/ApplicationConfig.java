package edu.java.configuration;

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
    @Bean
    @NestedConfigurationProperty
    @NotNull
    Scheduler scheduler,
    @NestedConfigurationProperty
    @NotNull
    StackoverflowProperties stackoverflowProperties,
    @NestedConfigurationProperty
    @NotNull
    GithubProperties githubProperties,
    @NestedConfigurationProperty
    @NotNull
    BotClient botProperties,
    @NestedConfigurationProperty
    @NotNull
    AccessType databaseAccessType,
    @NestedConfigurationProperty
    @NotNull
    RetryProperties retryProperties,
    @NestedConfigurationProperty
    @NotNull
    RateLimitProperty rateLimitProperty
) {
    public enum AccessType {
        JDBC,
        JOOQ,
        JPA
    }

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public record StackoverflowProperties(String domain, String apiUrl, String questions) {
    }

    public record GithubProperties(String domain, String apiUrl, String repos) {
    }

    public record BotClient(String url) {
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

}

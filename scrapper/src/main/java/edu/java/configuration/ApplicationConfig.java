package edu.java.configuration;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
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
    BotClient botProperties
) {
    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public record StackoverflowProperties(String url) {
    }

    public record GithubProperties(String url) {
    }

    public record BotClient(String url) {
    }
}

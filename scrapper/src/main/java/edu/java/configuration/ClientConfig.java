package edu.java.configuration;

import edu.java.configuration.retry.RetryPolicy;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;

@Validated
@ConfigurationProperties(prefix = "client", ignoreUnknownFields = false)
public record ClientConfig(
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
    @Bean
    public WebClient githubWebClient() {
        return WebClient
            .builder()
            .baseUrl(githubProperties().apiUrl())
            .build();
    }

    @Bean
    public WebClient stackoverflowWebClient() {
        return WebClient
            .builder()
            .baseUrl(stackoverflowProperties().apiUrl())
            .build();
    }

    @Bean
    public WebClient botWebClient() {
        return WebClient
            .builder()
            .baseUrl(botProperties().url())
            .build();
    }

    public record StackoverflowProperties(String domain, String apiUrl, String questions, RetryPolicy retryPolicy) {
    }

    public record GithubProperties(String domain, String apiUrl, String repos, RetryPolicy retryPolicy) {
    }

    public record BotClient(String url, RetryPolicy retryPolicy) {
    }
}

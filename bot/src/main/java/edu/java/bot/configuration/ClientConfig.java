package edu.java.bot.configuration;

import edu.java.bot.configuration.retry.RetryPolicy;
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
    @Bean
    ScrapperProperties scrapperProperties
) {

    @Bean
    public WebClient scrapperWebClient() {
        return WebClient
            .builder()
            .baseUrl(scrapperProperties().url())
            .build();
    }

    public record ScrapperProperties(String chat, String links, String tgChatId, String url, RetryPolicy retryPolicy) {
    }

}

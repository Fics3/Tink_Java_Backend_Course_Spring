package edu.java.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class ClientConfig {

    private final ApplicationConfig applicationConfig;

    @Bean
    public WebClient githubWebClient() {
        return WebClient
            .builder()
            .baseUrl(applicationConfig.githubProperties().repos())
            .build();
    }

    @Bean
    public WebClient stackoverflowWebClient() {
        return WebClient
            .builder()
            .baseUrl(applicationConfig.stackoverflowProperties().questions())
            .build();
    }

    @Bean
    public WebClient botWebClient() {
        return WebClient
            .builder()
            .baseUrl(applicationConfig.botProperties().url())
            .build();
    }
}


package edu.java.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {
    private static final String GITHUB_COM = "https://api.github.com/";
    private static final String STACKOVERFLOW = "https://api.stackexchange.com/";

    @Bean
    public WebClient githubWebClient() {
        return WebClient
            .builder()
            .baseUrl(GITHUB_COM)
            .build();
    }

    @Bean
    public WebClient stackoverflowWebClient() {
        return WebClient
            .builder()
            .baseUrl(STACKOVERFLOW)
            .build();
    }
}


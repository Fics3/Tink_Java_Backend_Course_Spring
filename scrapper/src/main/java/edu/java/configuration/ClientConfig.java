package edu.java.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {
    private static final String GITHUB_COM = "https://api.github.com/";
    private static final String STACKOVERFLOW = "https://api.stackexchange.com/";
    private static final String LOCALHOST_8080 = "http://localhost:8080";

    @Bean
    @Qualifier("github")
    public WebClient getGitHubClient() {
        return WebClient
            .builder()
            .baseUrl(GITHUB_COM)
            .build();
    }

    @Bean
    @Qualifier("stackoverflow")
    public WebClient getStackoverflowClient() {
        return WebClient
            .builder()
            .baseUrl(STACKOVERFLOW)
            .build();
    }

    @Bean
    @Qualifier("scrapper")
    public WebClient getScrapperClient() {
        return WebClient
            .builder()
            .baseUrl(LOCALHOST_8080)
            .build();
    }

    @Bean
    @Qualifier("bot")
    public WebClient boyClient() {
        return WebClient
            .builder()
            .baseUrl(LOCALHOST_8080)
            .build();
    }
}


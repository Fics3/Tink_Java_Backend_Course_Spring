package edu.java.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {
    private static final String GITHUB_COM = "https://api.github.com/";
    private static final String STACKOVERFLOW = "https://api.stackexchange.com/";

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
    public WebClient getStackoverflow() {
        return WebClient
            .builder()
            .baseUrl(STACKOVERFLOW)
            .build();
    }
}


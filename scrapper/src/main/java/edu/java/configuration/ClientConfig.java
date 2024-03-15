package edu.java.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Bean
    public WebClient githubWebClient(@Value("${app.github-properties.url}") String githubUrl) {
        return WebClient
            .builder()
            .baseUrl(githubUrl)
            .build();
    }

    @Bean
    public WebClient stackoverflowWebClient(@Value("${app.stackoverflow-properties.url}") String stackoverflowUrl) {
        return WebClient
            .builder()
            .baseUrl(stackoverflowUrl)
            .build();
    }

    @Bean
    public WebClient botWebClient(@Value("$app.bot-properties.url}") String botUrl) {
        return WebClient
            .builder()
            .baseUrl(botUrl)
            .build();
    }
}


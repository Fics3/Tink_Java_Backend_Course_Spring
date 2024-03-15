package edu.java.bot.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {
    @Bean
    public WebClient scrapperWebClient(@Value("${app.scrapper-properties.url}") String baseUrl) {
        return WebClient
            .builder()
            .baseUrl(baseUrl)
            .build();
    }
}

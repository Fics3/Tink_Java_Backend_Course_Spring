package edu.java.bot.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class ClientConfig {

    private final ApplicationConfig applicationConfig;

    @Bean
    public WebClient scrapperWebClient() {
        return WebClient
            .builder()
            .baseUrl(applicationConfig.scrapperProperties().url())
            .build();
    }
}

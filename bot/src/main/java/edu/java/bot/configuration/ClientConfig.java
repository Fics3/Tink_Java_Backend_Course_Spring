package edu.java.bot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {
    @Bean
    public WebClient scrapperWebClient(ApplicationConfig.ScrapperProperties scrapperProperties) {
        return WebClient
            .builder()
            .baseUrl(scrapperProperties.url())
            .build();
    }
}

package edu.java.bot.controller;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.service.NotificationService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    public TelegramBot telegramTestBot() {
        return mock(TelegramBot.class);
    }

    @Bean
    public NotificationService notificationService() {
        return mock(NotificationService.class);
    }

    @Bean
    WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

}

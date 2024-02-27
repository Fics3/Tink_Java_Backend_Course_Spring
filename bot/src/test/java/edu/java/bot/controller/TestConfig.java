package edu.java.bot.controller;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.service.NotificationService;
import edu.java.bot.service.commands.CommandService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;
import static org.mockito.Mockito.mock;

@Configuration
public class TestConfig {

    @Bean
    public TelegramBot telegramBot() {
        return mock(TelegramBot.class);
    }

    @Bean
    @Qualifier("commandMap")
    public Map<String, CommandService> commandMap(){
        return new HashMap<>();
    }

    @Bean
    public NotificationService notificationService() {
        return mock(NotificationService.class);
    }

}

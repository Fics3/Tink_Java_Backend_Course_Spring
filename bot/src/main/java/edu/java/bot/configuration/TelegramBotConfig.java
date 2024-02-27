package edu.java.bot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.service.commands.CommandService;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotConfig {

    @Bean
    public TelegramBot telegramBot(ApplicationConfig applicationConfig, Map<String, CommandService> commandMap) {
        TelegramBot telegramBot = new TelegramBot(applicationConfig.telegramToken());
        telegramBot.execute(setMyCommands(commandMap));

        return telegramBot;
    }

    private SetMyCommands setMyCommands(Map<String, CommandService> commandMap) {
        return new SetMyCommands(
            commandMap
                .values()
                .stream()
                .map(command -> new BotCommand(command.getName(), command.getDescription()))
                .toArray(BotCommand[]::new)
        );
    }
}

package edu.java.bot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.model.commands.CommandManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotConfig {

    @Bean
    public TelegramBot telegramBot(ApplicationConfig applicationConfig, CommandManager commandManager) {
        TelegramBot telegramBot = new TelegramBot(applicationConfig.telegramToken());
        telegramBot.execute(setMyCommands(commandManager));

        return telegramBot;
    }

    private SetMyCommands setMyCommands(CommandManager commandManager) {
        return new SetMyCommands(commandManager.getCommandMap().values().stream()
            .map(command -> new BotCommand(command.getName(), command.getDescription())
            ).toArray(BotCommand[]::new));
    }
}

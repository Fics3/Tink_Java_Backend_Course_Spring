package edu.java.bot.configuration;

import edu.java.bot.model.commands.CommandManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandConfiguration {

    @Bean
    CommandManager commandManager() {
        return new CommandManager();
    }

}

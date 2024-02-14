package edu.java.bot.configuration;

import edu.java.bot.service.commands.Command;
import edu.java.bot.service.commands.HelpCommand;
import edu.java.bot.service.commands.ListCommand;
import edu.java.bot.service.commands.StartCommand;
import edu.java.bot.service.commands.TrackCommand;
import edu.java.bot.service.commands.UntrackCommand;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandConfig {

    Command[] commands = {
        new StartCommand(),
        new HelpCommand(),
        new ListCommand(),
        new TrackCommand(),
        new UntrackCommand()
    };

    @Bean
    Map<String, Command> commandMap() {
        return Arrays.stream(commands).collect(Collectors.toMap(Command::getName, command -> command));
    }

}

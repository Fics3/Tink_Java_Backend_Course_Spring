package edu.java.bot.service.commands;

import edu.java.bot.service.NotificationService;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class HelpCommandService implements CommandService {
    private static final String NAME = "/help";
    private static final String DESCRIPTION = "отобразить все команды бота";

    @Override
    public String execute(long chatId, String message, NotificationService updateListener) {
        return updateListener.getCommandMap().values().stream()
            .map(value -> String.format("%s - %s", value.getName(), value.getDescription()))
            .collect(Collectors.joining("\n"));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}

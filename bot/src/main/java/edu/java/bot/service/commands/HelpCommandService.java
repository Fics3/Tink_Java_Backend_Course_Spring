package edu.java.bot.service.commands;

import edu.java.bot.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class HelpCommandService implements CommandService {
    private static final String NAME = "/help";
    private static final String DESCRIPTION = "show all bot commands";

    @Override
    public String execute(long chatId, String message, NotificationService updateListener) {
        return updateListener.getCommandMap().keySet().toString();
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

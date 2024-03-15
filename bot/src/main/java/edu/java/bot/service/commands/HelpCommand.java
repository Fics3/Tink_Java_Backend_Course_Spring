package edu.java.bot.service.commands;

import edu.java.bot.service.NotificationService;

public class HelpCommand implements Command {
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

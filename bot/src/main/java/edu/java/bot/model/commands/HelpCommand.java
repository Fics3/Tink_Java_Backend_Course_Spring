package edu.java.bot.model.commands;

import edu.java.bot.service.NotificationService;

public class HelpCommand implements Command {
    @Override
    public String execute(long chatId, String message, NotificationService updateListener) {
        return updateListener.getCommandManager().getCommandMap().keySet().toString();
    }

    @Override
    public String getName() {
        return "/help";
    }

    @Override
    public String getDescription() {
        return "show all bot commands";
    }
}

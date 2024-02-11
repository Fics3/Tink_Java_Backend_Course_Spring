package edu.java.bot.model.commands;

import edu.java.bot.service.NotificationService;

public interface Command {
    String execute(long chatId, String message, NotificationService notificationService);

    String getName();

    String getDescription();
}

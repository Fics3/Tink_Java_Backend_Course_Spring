package edu.java.bot.service.commands;

import edu.java.bot.service.NotificationService;

public interface CommandService {
    String execute(long chatId, String message, NotificationService notificationService);

    String getName();

    String getDescription();
}

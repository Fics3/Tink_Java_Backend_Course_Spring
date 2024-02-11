package edu.java.bot.service;

import edu.java.bot.model.User;
import edu.java.bot.model.commands.Command;
import edu.java.bot.model.commands.CommandManager;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter @Service
public class NotificationService {
    private final Map<Long, User> linkMap;

    private final CommandManager commandManager;

    public NotificationService(CommandManager commandManager) {
        linkMap = new HashMap<>();
        this.commandManager = commandManager;
    }

    public String getCommand(long chatId, String message) {
        try {
            String[] parsedMessage = message.split(" ");
            Command command = commandManager.getCommandMap().get(parsedMessage[0]);
            return command.execute(chatId, message, this);
        } catch (NullPointerException e) {
            return "Такой команды не существует, введите /help, чтобы увидеть список доступных команд";
        }
    }

}

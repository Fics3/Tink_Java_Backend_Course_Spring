package edu.java.bot.service;

import edu.java.bot.model.User;
import edu.java.bot.service.commands.Command;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class NotificationService {
    private static final String ERROR_MESSAGE = "Такой команды не существует, введите /help, чтобы увидеть список доступных команд";
    private final Map<Long, User> linkMap;
    private final Map<String, Command> commandMap;

    public NotificationService(Map<String, Command> commandMap) {
        linkMap = new HashMap<>();
        this.commandMap = commandMap;
    }

    public String getCommand(long chatId, String message) {
        try {
            String[] parsedMessage = message.split(" ");
            Command command = commandMap.get(parsedMessage[0]);
            return command.execute(chatId, message, this);
        } catch (NullPointerException e) {
            return ERROR_MESSAGE;
        }
    }

}

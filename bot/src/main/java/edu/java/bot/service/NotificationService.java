package edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.User;
import edu.java.bot.service.commands.Command;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class NotificationService {
    private static final String ERROR_MESSAGE =
        "Такой команды не существует, введите /help, чтобы увидеть список доступных команд";
    private final Map<Long, User> linkMap;
    private final Map<String, Command> commandMap;

    public NotificationService(Map<String, Command> commandMap) {
        linkMap = new HashMap<>();
        this.commandMap = commandMap;
    }

    public void processCommand(Update update, TelegramBot telegramBot) {
        long chatId = update.message().chat().id();
        String message = update.message().text();
        sendMessage(chatId, getCommand(chatId, message), telegramBot);
    }

    public String getCommand(long chatId, String message) {
        String[] parsedMessage = message.split(" ");
        Command command = commandMap.get(parsedMessage[0]);
        if (command == null) {
            return ERROR_MESSAGE;
        } else {
            return command.execute(chatId, message, this);
        }

    }

    private void sendMessage(long chatId, String messageText, TelegramBot telegramBot) {
        SendMessage message = new SendMessage(chatId, messageText);
        telegramBot.execute(message);
    }

}

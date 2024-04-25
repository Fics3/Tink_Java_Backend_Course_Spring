package edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.commands.CommandService;
import java.util.Map;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Getter
@Service
@Log4j2
public class NotificationService {
    private static final String ERROR_MESSAGE =
        "Такой команды не существует, введите /help, чтобы увидеть список доступных команд";
    private final Map<String, CommandService> commandMap;

    public NotificationService(@Qualifier("commandMap") Map<String, CommandService> commandMap) {
        this.commandMap = commandMap;
    }

    public void processCommand(Update update, TelegramBot telegramBot) {
        long chatId = update.message().chat().id();
        String message = update.message().text();
        sendMessage(chatId, getCommand(chatId, message), telegramBot);
    }

    public String getCommand(Long chatId, String message) {
        if (message == null) {
            return ERROR_MESSAGE;
        }
        String[] parsedMessage = message.split(" ");
        CommandService commandService = commandMap.get(parsedMessage[0]);
        if (commandService == null) {
            return ERROR_MESSAGE;
        } else {
            return commandService.execute(chatId, message, this);
        }

    }

    private void sendMessage(Long chatId, String messageText, TelegramBot telegramBot) {
        SendMessage message = new SendMessage(chatId, messageText);
        telegramBot.execute(message);
    }

    public boolean messageIsNull(Update update) {
        return update.message() == null;
    }
}

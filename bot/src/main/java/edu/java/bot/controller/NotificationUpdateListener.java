package edu.java.bot.controller;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.NotificationService;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@Getter
public class NotificationUpdateListener implements UpdatesListener {

    private final TelegramBot telegramBot;
    private final NotificationService notificationService;

    public NotificationUpdateListener(TelegramBot telegramBot, NotificationService notificationService) {
        this.telegramBot = telegramBot;
        this.notificationService = notificationService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            if (update.message() == null) {
                return;
            }
            String updateMessageText = update.message().text();
            long chatId = update.message().chat().id();
            processCommand(chatId, updateMessageText);
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processCommand(long chatId, String message) {
        sendMessage(chatId, notificationService.getCommand(chatId, message));
    }

    private void sendMessage(long chatId, String messageText) {
        SendMessage message = new SendMessage(chatId, messageText);
        telegramBot.execute(message);
    }
}

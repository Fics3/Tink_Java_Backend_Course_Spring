package edu.java.bot.controller;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.service.NotificationService;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;

@Log4j2
@Getter
@Controller
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
            notificationService.processCommand(update, telegramBot);
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}


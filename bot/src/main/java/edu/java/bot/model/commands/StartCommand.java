package edu.java.bot.model.commands;

import edu.java.bot.model.User;
import edu.java.bot.service.NotificationService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class StartCommand implements Command {

    @Override
    public String execute(long chatId, String message, NotificationService updateListener) {
        log.info("new user registered: " + chatId);
        updateListener.getLinkMap().put(chatId, new User(chatId));
        return "Hello World";
    }

    @Override
    public String getName() {
        return "/start";
    }

    @Override
    public String getDescription() {
        return "registration";
    }
}

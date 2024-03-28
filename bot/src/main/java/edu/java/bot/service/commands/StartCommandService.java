package edu.java.bot.service.commands;

import edu.java.bot.model.User;
import edu.java.bot.service.NotificationService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class StartCommandService implements CommandService {
    private static final String NAME = "/start";
    private static final String DESCRIPTION = "registration";
    private static final String WELCOME_MESSAGE = "Hello world";
    private static final String USER_REGISTERED = "new user registered: ";

    @Override
    public String execute(long chatId, String message, NotificationService updateListener) {
        log.info(USER_REGISTERED + chatId);
        updateListener.getLinkMap().put(chatId, new User(chatId));
        return WELCOME_MESSAGE;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}

package edu.java.bot.service.commands;

import edu.java.bot.service.NotificationService;
import edu.java.bot.service.commands.resourcesHandlers.ChainResourceHandler;
import edu.java.bot.service.commands.resourcesHandlers.GitHubHandler;
import edu.java.bot.service.commands.resourcesHandlers.StackOverflowHandler;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class TrackCommandService implements CommandService {
    private static final String NAME = "/track";
    private static final String DESCRIPTION = "add URL to list of tracking links /track {URL}";
    private static final String ERROR_MESSAGE = "Неверный формат команды: /track {URL}";
    private static final String NO_REGISTERED_MESSAGE = "Для отслеживания ссылок необходимо зарегестрироваться /start";
    private final ChainResourceHandler chainResourceHandler;

    public TrackCommandService() {
        this.chainResourceHandler = initResourceHandler();
    }

    private ChainResourceHandler initResourceHandler() {
        ChainResourceHandler chain = new GitHubHandler();
        chain.linkWith(new StackOverflowHandler());
        return chain;
    }

    @Override
    public String execute(long chatId, String message, NotificationService updateListener) {
        if (updateListener.getLinkMap().get(chatId) == null) {
            return NO_REGISTERED_MESSAGE;
        }
        try {
            String resource = message.split(" ")[1];
            return chainResourceHandler.handleLink(chatId, resource, updateListener);
        } catch (ArrayIndexOutOfBoundsException e) {
            return ERROR_MESSAGE;
        }
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


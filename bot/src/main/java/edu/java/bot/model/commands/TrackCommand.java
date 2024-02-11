package edu.java.bot.model.commands;

import edu.java.bot.model.commands.resourcesHandlers.ChainResourceHandler;
import edu.java.bot.model.commands.resourcesHandlers.GitHubHandler;
import edu.java.bot.model.commands.resourcesHandlers.StackOverflowHandler;
import edu.java.bot.service.NotificationService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class TrackCommand implements Command {

    private final ChainResourceHandler chainResourceHandler;

    public TrackCommand() {
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
            return "Для отслеживания ссылок необходимо зарегестрироваться /start";
        }
        try {
            String resource = message.split(" ")[1];
            return chainResourceHandler.handleLink(chatId, resource, updateListener);
        } catch (ArrayIndexOutOfBoundsException e) {
            return "Неверный формат команды: /track {URL}";
        }
    }

    @Override
    public String getName() {
        return "/track";
    }

    @Override
    public String getDescription() {
        return "add URL to list of tracking links /track {URL}";
    }
}


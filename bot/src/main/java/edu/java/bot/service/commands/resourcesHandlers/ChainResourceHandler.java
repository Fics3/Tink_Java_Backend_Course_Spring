package edu.java.bot.service.commands.resourcesHandlers;

import edu.java.bot.service.NotificationService;
import java.net.URISyntaxException;
import java.util.Objects;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Getter
@Log4j2
public abstract class ChainResourceHandler {
    private static final String WRONG_FORMAT = "Неверный формат ссылки";
    private static final String ANSWER = "Ссылка добавлена";
    private ChainResourceHandler nextHandler;

    public void linkWith(ChainResourceHandler chainResourceHandler) {
        this.nextHandler = chainResourceHandler;
    }

    public String handleLink(long chatId, String message, NotificationService updateListener) {
        try {
            Link newLink = Link.parse(message);
            if (Objects.equals(newLink.uri().getHost(), getHost())) {
                updateListener.getLinkMap().get(chatId).getLinks().add(newLink);
            } else if (nextHandler != null) {
                nextHandler.handleLink(chatId, message, updateListener);
            }
        } catch (URISyntaxException e) {
            return WRONG_FORMAT;
        }
        return ANSWER;
    }

    public abstract String getHost();
}

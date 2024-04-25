package edu.java.bot.service.commands.resourcesHandlers;

import edu.java.bot.service.ScrapperService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Getter
@Log4j2
@RequiredArgsConstructor
public abstract class ChainResourceHandler {
    private static final String WRONG_FORMAT = "Неверный формат ссылки";
    private static final String ANSWER = "Ссылка добавлена";
    private final ScrapperService scrapperService;
    private ChainResourceHandler nextHandler;

    public void linkWith(ChainResourceHandler chainResourceHandler) {
        this.nextHandler = chainResourceHandler;
    }

    public String handleLink(long chatId, String message) {
        try {
            URI newLink = new URI(message);
            if (Objects.equals(newLink.getHost(), getHost())) {
                scrapperService.addLink(chatId, newLink);
            } else if (nextHandler != null) {
                nextHandler.handleLink(chatId, message);
            } else {
                return WRONG_FORMAT;
            }
            return ANSWER;
        } catch (URISyntaxException exception) {
            return WRONG_FORMAT;
        }
    }

    public abstract String getHost();
}

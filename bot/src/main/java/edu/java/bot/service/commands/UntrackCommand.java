package edu.java.bot.service.commands;

import edu.java.bot.service.NotificationService;
import java.util.Objects;

public class UntrackCommand implements Command {
    private static final String NAME = "/untrack";
    private static final String DESCRIPTION = "delete link from tracking links /untrack {URL}";
    private static final String NO_REGISTERED_MESSAGE = "Для удаления ссылок необходимо зарегестрироваться /start";
    private static final String WRONG_FORMAT_MESSAGE = "Неверный формат команды: /untrack {URL}";
    private static final String ANSWER = "Для просмотра ваших ссылок введите /list";

    @Override
    public String execute(long chatId, String message, NotificationService updateListener) {
        if (updateListener.getLinkMap().get(chatId) == null) {
            return NO_REGISTERED_MESSAGE;
        }
        try {
            String resource = message.split(" ")[1];
            if (updateListener.getLinkMap().get(chatId) != null) {
                updateListener.getLinkMap().get(chatId).getLinks()
                    .removeIf(link -> Objects.equals(link.uri().toString(), resource));
            }
        } catch (
            ArrayIndexOutOfBoundsException e) {
            return WRONG_FORMAT_MESSAGE;
        }
        return ANSWER;
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

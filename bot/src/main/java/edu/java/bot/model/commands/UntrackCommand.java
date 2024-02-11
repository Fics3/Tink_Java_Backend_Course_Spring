package edu.java.bot.model.commands;

import edu.java.bot.service.NotificationService;
import java.util.Objects;

public class UntrackCommand implements Command {
    @Override
    public String execute(long chatId, String message, NotificationService updateListener) {
        if (updateListener.getLinkMap().get(chatId) == null) {
            return "Для удаления ссылок необходимо зарегестрироваться /start";
        }
        try {
            String resource = message.split(" ")[1];
            if (updateListener.getLinkMap().get(chatId) != null) {
                updateListener.getLinkMap().get(chatId).getLinks()
                    .removeIf(link -> Objects.equals(link.uri().toString(), resource));
            }
        } catch (
            ArrayIndexOutOfBoundsException e) {
            return "Неверный формат команды: /untrack {URL}";
        }
        return "Для просмотра ваших ссылок введите /list";
    }

    @Override
    public String getName() {
        return "/untrack";
    }

    @Override
    public String getDescription() {
        return "delete link from tracking links /untrack {URL}";
    }
}

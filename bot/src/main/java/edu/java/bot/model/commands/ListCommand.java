package edu.java.bot.model.commands;

import edu.java.bot.model.User;
import edu.java.bot.service.NotificationService;
import java.util.stream.Collectors;

public class ListCommand implements Command {
    @Override
    public String execute(long chatId, String message, NotificationService updateListener) {
        if (updateListener.getLinkMap().get(chatId) == null) {
            return "Для просмотра ссылок необходимо зарегестрироваться /start";
        }
        return getLinks(updateListener.getLinkMap().get(chatId));
    }

    private String getLinks(User user) {
        String result;
        if (user.getLinks().isEmpty()) {
            result = "Нет сохраненных ссылок";
        } else {
            result = "Сохраненные ссылки:\n" + user.getLinks().stream()
                .map(link -> link.uri().toString())
                .collect(Collectors.joining("\n"));
        }
        return result;
    }

    @Override
    public String getName() {
        return "/list";
    }

    @Override
    public String getDescription() {
        return "show all tracking links";
    }
}

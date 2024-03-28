package edu.java.bot.service.commands;

import edu.java.bot.model.User;
import edu.java.bot.service.NotificationService;
import java.util.stream.Collectors;

public class ListCommandService implements CommandService {
    private static final String NAME = "/list";
    private static final String DESCRIPTION = "show all bot commands";
    private static final String NO_SAVED_LINKS = "Нет сохраненных ссылок";
    private static final String SAVED_LINKS = "Сохраненные ссылки:\n";
    private static final String NO_REGISTERED_MESSAGE = "Для просмотра ссылок необходимо зарегестрироваться /start";

    @Override
    public String execute(long chatId, String message, NotificationService updateListener) {
        if (updateListener.getLinkMap().get(chatId) == null) {
            return NO_REGISTERED_MESSAGE;
        }
        return getLinks(updateListener.getLinkMap().get(chatId));
    }

    private String getLinks(User user) {
        String result;
        if (user.getLinks().isEmpty()) {
            result = NO_SAVED_LINKS;
        } else {
            result = SAVED_LINKS + user.getLinks().stream()
                .map(link -> link.uri().toString())
                .collect(Collectors.joining("\n"));
        }
        return result;
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

package edu.java.bot.service.commands;

import edu.java.bot.service.NotificationService;
import edu.java.bot.service.ScrapperService;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class ListCommandService implements CommandService {
    private static final String NAME = "/list";
    private static final String DESCRIPTION = "отобразить все отслеживаемые ссылки";
    private static final String NO_SAVED_LINKS = "Нет сохраненных ссылок";
    private static final String SAVED_LINKS = "Сохраненные ссылки:\n";
    private static final String NO_REGISTERED_MESSAGE = "Для просмотра ссылок необходимо зарегестрироваться /start";

    private final ScrapperService scrapperService;

    @Override
    public String execute(long chatId, String message, NotificationService updateListener) {
        return getLinks(chatId);
    }

    private String getLinks(Long tgChatId) {
        try {
            String result;

            var linksResponse = scrapperService.getAllLinks(tgChatId);
            if (linksResponse.links().isEmpty()) {
                result = NO_SAVED_LINKS;
            } else {
                result = SAVED_LINKS + linksResponse.links().stream()
                    .map(link -> link.url().toString())
                    .collect(Collectors.joining("\n"));
            }
            return result;
        } catch (WebClientResponseException e) {
            return NO_REGISTERED_MESSAGE;
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

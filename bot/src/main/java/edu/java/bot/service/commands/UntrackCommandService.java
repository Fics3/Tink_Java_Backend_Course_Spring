package edu.java.bot.service.commands;

import edu.java.bot.service.NotificationService;
import edu.java.bot.service.ScrapperService;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class UntrackCommandService implements CommandService {
    private static final String NAME = "/untrack";
    private static final String DESCRIPTION = "перестать отслеживать ссылку /untrack {URL}";
    private static final String NO_REGISTERED_MESSAGE = "Для удаления ссылок необходимо зарегестрироваться /start";
    private static final String WRONG_FORMAT_MESSAGE = "Неверный формат команды: /untrack {URL}";
    private static final String URI_SYNTAX = "Неправильный тип ссылки";
    private static final String ANSWER = "Для просмотра ваших ссылок введите /list";
    private final ScrapperService scrapperService;

    @Override
    public String execute(long chatId, String message, NotificationService updateListener) {
        try {
            String resource = message.split(" ")[1];
            scrapperService.deleteLink(chatId, new URI(resource));
        } catch (ArrayIndexOutOfBoundsException e) {
            return WRONG_FORMAT_MESSAGE;
        } catch (URISyntaxException e) {
            return URI_SYNTAX;
        } catch (WebClientResponseException e) {
            return NO_REGISTERED_MESSAGE;
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

package edu.java.bot.service.commands;

import edu.java.bot.service.NotificationService;
import edu.java.bot.service.ScrapperService;
import edu.java.bot.service.commands.resourcesHandlers.ChainResourceHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Log4j2
@Getter
@Service
@RequiredArgsConstructor
public class TrackCommandService implements CommandService {
    private static final String NAME = "/track";
    private static final String DESCRIPTION = "начать отслеживать ссылку /track {URL}";
    private static final String ERROR_MESSAGE = "Неверный формат команды: /track {URL}";
    private static final String NO_REGISTERED_MESSAGE = "Для отслеживания ссылок необходимо зарегестрироваться /start";
    private static final String LINK_ALREADY_EXIST = "Вы уже отслеживаете такую ссылку";
    private static final String DEFAULT_ERROR = "Ошибка, попробуйте позже";
    private final ChainResourceHandler chainResourceHandler;
    private final ScrapperService scrapperService;

    @Override
    public String execute(long chatId, String message, NotificationService updateListener) {
        try {
            String resource = message.split(" ")[1];
            return chainResourceHandler.handleLink(chatId, resource);
        } catch (ArrayIndexOutOfBoundsException e) {
            return ERROR_MESSAGE;
        } catch (WebClientResponseException e) {
            return switch (e.getStatusCode()) {
                case HttpStatus.BAD_REQUEST -> NO_REGISTERED_MESSAGE;
                case HttpStatus.CONFLICT -> LINK_ALREADY_EXIST;
                default -> DEFAULT_ERROR;
            };
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


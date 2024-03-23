package edu.java.bot.service.commands;

import edu.java.bot.service.NotificationService;
import edu.java.bot.service.ScrapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Log4j2
@Service
@RequiredArgsConstructor
public class StartCommandService implements CommandService {
    private static final String NAME = "/start";
    private static final String DESCRIPTION = "регистрация";
    private static final String WELCOME_MESSAGE =
        "Привет, я умею отслеживать вопросы на stackoverflow и репозитории на github";
    private final ScrapperService scrapperService;

    @Override
    public String execute(long chatId, String message, NotificationService updateListener) {
        try {
            scrapperService.registerChat(chatId);
        } catch (WebClientResponseException e) {
            return "Вы уже зарегестрированы";
        }
        return WELCOME_MESSAGE;
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

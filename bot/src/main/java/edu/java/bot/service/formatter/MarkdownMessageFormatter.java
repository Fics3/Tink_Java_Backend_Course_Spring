package edu.java.bot.service.formatter;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.example.dto.LinkUpdateRequest;
import org.springframework.stereotype.Service;

@Service
public class MarkdownMessageFormatter implements MessageFormatter {
    @Override
    public SendMessage formatMessage(LinkUpdateRequest linkUpdateRequest, Long tgChatId) {
        String message =
            bold("Поступили обновления по ссылке!") + "\n"
                + "Ссылка: " + italic(linkUpdateRequest.url().toString()) + "\n"
                + linkUpdateRequest.description();
        return new SendMessage(tgChatId, message).parseMode(ParseMode.Markdown);
    }

    @Override
    public String bold(String string) {
        return "**" + string + "**";
    }

    @Override
    public String italic(String string) {
        return "*" + string + "*";
    }

    @Override
    public String strike(String string) {
        return "~~" + string + "~~";
    }
}

package edu.java.bot.service.formatter;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.example.dto.LinkUpdateRequest;
import org.springframework.stereotype.Service;

@Service
public class MarkdownMessageFormatter implements MessageFormatter {
    @Override
    public SendMessage formatUpdateMessage(LinkUpdateRequest linkUpdateRequest, Long tgChatId) {
        String message = String.format(
                """
                        *Поступили обновления по ссылке!*
                        Ссылка: [%s](%s)
                        %s""",
            linkUpdateRequest.url().toString(),
            linkUpdateRequest.url(),
            linkUpdateRequest.description()
        );

        return new SendMessage(tgChatId, message).parseMode(ParseMode.Markdown);
    }
}

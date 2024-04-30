package edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.service.formatter.MessageFormatter;
import lombok.RequiredArgsConstructor;
import org.example.dto.LinkUpdateRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateService {

    private final TelegramBot telegramBot;
    private final MessageFormatter markdownMessageFormatter;

    public void processUpdate(LinkUpdateRequest linkUpdateRequest, Long id) {
        telegramBot.execute(markdownMessageFormatter.formatUpdateMessage(linkUpdateRequest, id));
    }
}

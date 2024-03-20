package edu.java.bot.service.formatter;

import com.pengrad.telegrambot.request.SendMessage;
import org.example.dto.LinkUpdateRequest;

public interface MessageFormatter {

    SendMessage formatMessage(LinkUpdateRequest linkUpdateRequest, Long tgChatId);

    String bold(String string);

    String italic(String string);

    String strike(String string);

}

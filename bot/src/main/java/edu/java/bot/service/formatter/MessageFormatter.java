package edu.java.bot.service.formatter;

import com.pengrad.telegrambot.request.SendMessage;
import org.example.dto.LinkUpdateRequest;

public interface MessageFormatter {

    SendMessage formatUpdateMessage(LinkUpdateRequest linkUpdateRequest, Long tgChatId);
}

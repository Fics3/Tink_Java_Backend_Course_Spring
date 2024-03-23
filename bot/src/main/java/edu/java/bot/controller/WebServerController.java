package edu.java.bot.controller;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.service.formatter.MessageFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.dto.LinkUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequestMapping("/updates")
@RequiredArgsConstructor
public class WebServerController {

    private final TelegramBot telegramBot;
    private final MessageFormatter markdownMessageFormatter;

    @PostMapping
    public ResponseEntity<String> processUpdate(@RequestBody LinkUpdateRequest linkUpdateRequest) {
        for (var id : linkUpdateRequest.tgChatIds()) {
            telegramBot.execute(markdownMessageFormatter.formatMessage(linkUpdateRequest, id));
        }

        return ResponseEntity.ok("Обноавление обработано");
    }

}

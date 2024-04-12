package edu.java.controller;

import edu.java.service.ChatService;
import lombok.RequiredArgsConstructor;
import io.micrometer.core.instrument.Counter;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tg-chat/{id}")
@RequiredArgsConstructor
public class TelegramChatController {

    private final ChatService chatService;
    private final Counter messageCounter;

    @PostMapping
    public String registerChat(@PathVariable Long id) {
        messageCounter.increment();
        chatService.add(id);
        return "Чат зарегестрирован";
    }

    @DeleteMapping
    public String deleteChat(@PathVariable Long id) {
        messageCounter.increment();
        chatService.remove(id);
        return "Чат успешно удален";
    }

}

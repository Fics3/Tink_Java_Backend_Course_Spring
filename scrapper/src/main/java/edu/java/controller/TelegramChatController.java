package edu.java.controller;

import edu.java.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tg-chat/{id}")
public class TelegramChatController {

    @Autowired
    private ChatService jooqChatService;

    @PostMapping
    public String registerChat(@PathVariable Long id) {
        jooqChatService.add(id);
        return "Чат зарегестрирован";
    }

    @DeleteMapping
    public String deleteChat(@PathVariable Long id) {
        jooqChatService.remove(id);
        return "Чат успешно удален";
    }

}

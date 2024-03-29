package edu.java.controller;

import edu.java.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<String> registerChat(@PathVariable Long id) {
        chatService.add(id);
        return ResponseEntity.ok("Чат зарегестрирован");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteChat(@PathVariable Long id) {
        chatService.remove(id);
        return ResponseEntity.ok("Чат успешно удален");
    }

}

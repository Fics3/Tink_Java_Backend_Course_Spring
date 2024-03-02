package edu.java.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tg-chat/{id}")
public class TelegramChatController {

    @PostMapping
    public ResponseEntity<Object> registerChat(@PathVariable int id) {
        return ResponseEntity.ok("Чат зарегестрирован");
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteChat(@PathVariable int id) {
        return ResponseEntity.ok("Чат успешно удален");
    }

}

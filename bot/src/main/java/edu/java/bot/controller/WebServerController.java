package edu.java.bot.controller;

import org.example.dto.LinkUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
public class WebServerController {
    @PostMapping
    public ResponseEntity<Object> processUpdate(@RequestBody LinkUpdateRequest linkUpdateRequest) {
        return ResponseEntity.ok("Обноавление обработано");
    }
}

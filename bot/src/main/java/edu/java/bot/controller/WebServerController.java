package edu.java.bot.controller;

import lombok.extern.log4j.Log4j2;
import org.example.dto.LinkUpdateRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequestMapping("/updates")
public class WebServerController {
    @PostMapping
    public String processUpdate(@RequestBody LinkUpdateRequest linkUpdateRequest) {
        log.info(linkUpdateRequest.toString());
        return "Обноавление обработано";
    }
}

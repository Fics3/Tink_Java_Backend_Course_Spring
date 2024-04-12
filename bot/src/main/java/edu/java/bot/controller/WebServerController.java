package edu.java.bot.controller;

import edu.java.bot.service.UpdateService;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.dto.LinkUpdateRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequestMapping("/updates")
@RequiredArgsConstructor
public class WebServerController {

    private final UpdateService updateService;
    private final Counter messageCounter;

    @PostMapping
    public String processUpdate(@RequestBody LinkUpdateRequest linkUpdateRequest) {
        for (var id : linkUpdateRequest.tgChatIds()) {
            updateService.processUpdate(linkUpdateRequest, id);
            messageCounter.increment();
        }

        return "Обноавление обработано";
    }

}

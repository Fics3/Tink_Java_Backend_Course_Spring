package edu.java.service;

import edu.java.service.updateSender.BotUpdateSender;
import lombok.RequiredArgsConstructor;
import org.example.dto.LinkUpdateRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final BotUpdateSender botUpdateSender;

    public void sendNotification(LinkUpdateRequest linkUpdateRequest) {
        botUpdateSender.sendUpdate(linkUpdateRequest);
    }
}

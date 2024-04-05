package edu.java.service;

import edu.java.client.BotClient;
import edu.java.configuration.ApplicationConfig;
import lombok.RequiredArgsConstructor;
import org.example.dto.LinkUpdateRequest;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final ApplicationConfig applicationConfig;
    private final ScrapperQueueProducer scrapperQueueProducer;
    private final BotClient botClient;

    public void sendNotification(LinkUpdateRequest linkUpdateRequest) {
        if (applicationConfig.isUseQueue()) {
            scrapperQueueProducer.send(linkUpdateRequest);
        } else {
            botClient.sendUpdate(linkUpdateRequest).block();
        }
    }
}

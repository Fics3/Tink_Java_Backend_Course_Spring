package edu.java.service.updateSender;

import edu.java.client.BotClient;
import lombok.RequiredArgsConstructor;
import org.example.dto.LinkUpdateRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@ConditionalOnMissingBean(KafkaBotUpdateSender.class)
public class RestBotUpdateSender implements BotUpdateSender {

    private final BotClient botClient;

    @Override
    public void sendUpdate(LinkUpdateRequest linkUpdateRequest) {
        botClient.sendUpdate(linkUpdateRequest).block();
    }
}

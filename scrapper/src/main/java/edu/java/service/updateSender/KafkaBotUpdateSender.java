package edu.java.service.updateSender;

import edu.java.configuration.ApplicationConfig;
import lombok.RequiredArgsConstructor;
import org.example.dto.LinkUpdateRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(prefix = "app", name = "is-use-queue", havingValue = "true")
public class KafkaBotUpdateSender implements BotUpdateSender {

    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;
    private final ApplicationConfig applicationConfig;

    public void sendUpdate(LinkUpdateRequest linkUpdateRequest) {
        kafkaTemplate.send(applicationConfig.kafkaProperties().topic().name(), linkUpdateRequest);
    }

}

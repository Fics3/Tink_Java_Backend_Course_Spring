package edu.java.bot.service;

import edu.java.bot.configuration.ApplicationConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.dto.LinkUpdateRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class KafkaNotificationProcessorService {

    private final UpdateService updateService;
    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;
    private final ApplicationConfig applicationConfig;

    @KafkaListener(id = "myId",
                   topics = "${app.kafka-properties.topic.name}",
                   containerFactory = "linkUpdateKafkaListenerFactory")
    public void listen(LinkUpdateRequest linkUpdateRequest) {
        log.info("Сообщение от кафки: " + linkUpdateRequest);
        try {
            for (var id : linkUpdateRequest.tgChatIds()) {

                updateService.processUpdate(linkUpdateRequest, id);
            }
        } catch (Exception e) {
            log.info("Битое сообщение -> DLQ: ");
            kafkaTemplate.send(applicationConfig.kafkaProperties().dlqTopic().name(), linkUpdateRequest);
        }
    }
}

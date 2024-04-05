package edu.java.service;

import edu.java.configuration.ApplicationConfig;
import lombok.RequiredArgsConstructor;
import org.example.dto.LinkUpdateRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ScrapperQueueProducer {

    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;
    private final ApplicationConfig applicationConfig;

    public void send(LinkUpdateRequest linkUpdateRequest) {
        kafkaTemplate.send(applicationConfig.kafkaProperties().topic().name(), linkUpdateRequest);
    }

}

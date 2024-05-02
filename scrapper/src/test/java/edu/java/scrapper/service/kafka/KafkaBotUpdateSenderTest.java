package edu.java.scrapper.service.kafka;

import edu.java.configuration.ApplicationConfig;
import edu.java.scrapper.IntegrationTest;
import edu.java.service.updateSender.KafkaBotUpdateSender;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.example.dto.LinkUpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaBotUpdateSenderTest extends IntegrationTest {

    @Mock
    private KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;

    @Test
    void testSendUpdate() {
        // Given
        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(
            UUID.randomUUID(),
            URI.create("https://github.com/spring-projects/spring-boot/issues/36946"),
            "lalalala",
            List.of(1L, 2L)
        );
        // When
        ApplicationConfig applicationConfig = new ApplicationConfig(
            null,
            null,
            new ApplicationConfig.KafkaProperties(new ApplicationConfig.KafkaProperties.Topic(
                "topic-name",
                1,
                1
            )),
            true
        );
        KafkaBotUpdateSender kafkaBotUpdateSender = new KafkaBotUpdateSender(kafkaTemplate, applicationConfig);
        kafkaBotUpdateSender.sendUpdate(linkUpdateRequest);

        // Then
        verify(kafkaTemplate).send(any(String.class), any(LinkUpdateRequest.class));
    }
}

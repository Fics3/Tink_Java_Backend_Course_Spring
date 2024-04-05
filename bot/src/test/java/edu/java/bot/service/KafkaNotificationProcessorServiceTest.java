package edu.java.bot.service;

import edu.java.bot.configuration.ApplicationConfig;
import java.net.URI;
import java.util.Collections;
import java.util.UUID;
import org.example.dto.LinkUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KafkaNotificationProcessorServiceTest {

    @Mock
    private UpdateService updateService;

    @Mock
    private KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;

    @Mock
    private ApplicationConfig applicationConfig;

    @InjectMocks
    private KafkaNotificationProcessorService notificationProcessorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListen_Successful() {
        // Arrange
        LinkUpdateRequest linkUpdateRequest =
            new LinkUpdateRequest(
                UUID.randomUUID(),
                URI.create("example.com"),
                null,
                Collections.singletonList(123L)
            );

        // Act
        notificationProcessorService.listen(linkUpdateRequest);

        // Assert
        verify(updateService).processUpdate(any(), anyLong());
        verify(kafkaTemplate, times(0)).send(any(), any());
    }

    @Test
    void testListen_ExceptionOccurs_MessageSentToDLQ() {
        // Arrange
        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(null, null, null, null);
        doThrow(new RuntimeException("Test Exception")).when(updateService).processUpdate(any(), anyLong());
        // Создание поддельного объекта DlqTopic
        ApplicationConfig.KafkaProperties.DlqTopic dlqTopic = new ApplicationConfig.KafkaProperties.DlqTopic(
            "dlq-topic",
            1,
            1
        );
        // Создание поддельного объекта KafkaProperties
        ApplicationConfig.KafkaProperties kafkaProperties = mock(ApplicationConfig.KafkaProperties.class);
        when(kafkaProperties.dlqTopic()).thenReturn(dlqTopic);
        // Настройка applicationConfig для возврата поддельного объекта KafkaProperties
        when(applicationConfig.kafkaProperties()).thenReturn(kafkaProperties);

        // Act
        notificationProcessorService.listen(linkUpdateRequest);

        // Assert
        verify(kafkaTemplate).send("dlq-topic", linkUpdateRequest);
    }
}

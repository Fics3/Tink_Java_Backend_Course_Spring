package edu.java.bot.service.kafka;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.service.UpdateService;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.dto.LinkUpdateRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.TestPropertySource;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVERS}",
    "app.kafka-properties.dlq-topic.name=topic_dlq",
    "app.kafka-properties.dlq-topic.partitions-num=1",
    "app.kafka-properties.dlq-topic.replicas-num=1"
})
class KafkaNotificationProcessorServiceTest extends KafkaIntegrationTest {

    @MockBean
    private TelegramBot telegramBot;
    @MockBean
    private KafkaTemplate<String, LinkUpdateRequest> kafkaTemplateDlq;
    private KafkaTemplate<String, LinkUpdateRequest> testKafkaTemplate;
    @MockBean
    private UpdateService updateService;

    @BeforeAll
    static void setUpBeforeClass() {
        KAFKA.start();
    }

    @BeforeEach
    void setup() {
        openMocks(this);
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        testKafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
    }

    @Test
    void testListen_correctRequest() {
        // Arrange
        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(
            UUID.randomUUID(),
            URI.create("https://github.com/spring-projects/spring-boot/issues/36946"),
            "lalalala",
            List.of(1L, 2L)
        );

        // Act
        testKafkaTemplate.send("topic1", linkUpdateRequest);

        // Assert
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            verify(updateService, times(2)).processUpdate(any(), anyLong());
        });
        verifyNoInteractions(kafkaTemplateDlq);
    }

    @Test
    void testListen_nullRequest() throws InterruptedException {
        //Act
        testKafkaTemplate.send("topic1", "test", null);

        Thread.sleep(1000);

        // Assert
        verifyNoInteractions(updateService);
    }

    @Test
    void testListen_testSendToDlqWithException() {
        // Arrange
        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(
            UUID.randomUUID(),
            URI.create("https://github.com/spring-projects/spring-boot/issues/36946"),
            "lalalala",
            List.of(1L, 2L)
        );
        String errorMessage = "Error message";
        doThrow(new RuntimeException(errorMessage)).when(updateService).processUpdate(any(), anyLong());

        // Act
        testKafkaTemplate.send("topic1", linkUpdateRequest);

        // Assert
        verify(kafkaTemplateDlq, times(2)).isTransactional();
        //2, потому что так внутри DLPR работает
    }
}

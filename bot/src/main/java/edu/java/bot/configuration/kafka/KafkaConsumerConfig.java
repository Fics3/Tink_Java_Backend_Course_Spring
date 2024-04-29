package edu.java.bot.configuration.kafka;

import edu.java.bot.configuration.ApplicationConfig;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.dto.LinkUpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class KafkaConsumerConfig {

    private final ApplicationConfig applicationConfig;
    @Value("${spring.kafka.bootstrapServers}")
    private String boostrapServers;

    @Bean
    public ConsumerFactory<String, LinkUpdateRequest> consumerFactory() {
        ErrorHandlingDeserializer<LinkUpdateRequest> valueDeserializer = new ErrorHandlingDeserializer<>(
            new JsonDeserializer<>(LinkUpdateRequest.class));
        return new DefaultKafkaConsumerFactory<>(
            configPropsConsumer(),
            new StringDeserializer(),
            valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LinkUpdateRequest> linkUpdateKafkaListenerFactory(
        ConsumerFactory<String, LinkUpdateRequest> consumerFactory,
        DefaultErrorHandler defaultErrorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, LinkUpdateRequest> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(defaultErrorHandler);
        return factory;
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate) {
        return new DefaultErrorHandler(
            new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (consumerRecord, exception) -> new TopicPartition(
                    applicationConfig.kafkaProperties().dlqTopic().name(),
                    0
                )
            ));
    }

    private Map<String, Object> configPropsConsumer() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, LinkUpdateRequest.class);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "myId");
        return configProps;
    }
}

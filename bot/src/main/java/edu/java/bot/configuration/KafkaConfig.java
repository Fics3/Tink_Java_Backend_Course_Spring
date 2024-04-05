package edu.java.bot.configuration;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.dto.LinkUpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class KafkaConfig {

    private final ApplicationConfig applicationConfig;
    @Value("${spring.kafka.bootstrapServers}")
    private String boostrapServers;

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(applicationConfig.kafkaProperties().topic().name())
            .partitions(applicationConfig.kafkaProperties().topic().partitionsNum())
            .replicas(applicationConfig.kafkaProperties().topic().replicasNum())
            .build();
    }

    @Bean
    public NewTopic dlqTopic() {
        return TopicBuilder.name(applicationConfig.kafkaProperties().dlqTopic().name())
            .replicas(applicationConfig.kafkaProperties().dlqTopic().replicasNum())
            .partitions(applicationConfig.kafkaProperties().dlqTopic().partitionsNum())
            .build();
    }

    @Bean
    public Map<String, Object> configProps() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return configProps;
    }

    @Bean
    public ProducerFactory<String, LinkUpdateRequest> producerFactory() {
        return new DefaultKafkaProducerFactory<>(configProps());
    }

    @Bean
    public KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, LinkUpdateRequest> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // Разрешить десериализацию ваших классов DTO
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "myId");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
            new JsonDeserializer<>(LinkUpdateRequest.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LinkUpdateRequest> linkUpdateKafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, LinkUpdateRequest> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}

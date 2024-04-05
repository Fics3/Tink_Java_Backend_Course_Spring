package edu.java.configuration;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @Bean
    @NestedConfigurationProperty
    @NotNull
    Scheduler scheduler,
    @NestedConfigurationProperty
    @NotNull
    AccessType databaseAccessType,
    @NestedConfigurationProperty
    @NotNull
    KafkaProperties kafkaProperties,
    boolean isUseQueue
) {
    public enum AccessType {
        JDBC,
        JOOQ,
        JPA
    }

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public record KafkaProperties(Topic topic) {
        public record Topic(String name, Integer partitionsNum, Integer replicasNum) {
        }
    }
}

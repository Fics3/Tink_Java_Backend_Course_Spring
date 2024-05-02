package edu.java.bot.configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricConfig {

    @Bean
    public Counter messageCounter(MeterRegistry meterRegistry) {
        return meterRegistry.counter("message_processed_count");
    }
}

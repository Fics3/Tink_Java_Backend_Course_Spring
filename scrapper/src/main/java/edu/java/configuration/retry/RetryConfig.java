package edu.java.configuration.retry;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetryConfig {

    @Bean
    @Qualifier("retryBuilder")
    public Map<RetryPolicy.BackoffStrategy, RetryBuilder> retryBuilderMap(List<RetryBuilder> retryBuilders) {
        return retryBuilders.stream()
            .collect(Collectors.toMap(RetryBuilder::backoffStrategy, retryBuilder -> retryBuilder));
    }

}

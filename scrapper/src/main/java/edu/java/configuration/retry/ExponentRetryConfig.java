package edu.java.configuration.retry;

import edu.java.configuration.ApplicationConfig;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(prefix = "app", name = "retry-properties.backoff-strategy", havingValue = "exponent")
@Log4j2
public class ExponentRetryConfig {

    private final ApplicationConfig applicationConfig;

    @Bean
    public Retry retry() {
        List<Integer> statusCodes = applicationConfig.retryProperties().retryStatusCodes();
        return Retry.backoff(
            applicationConfig.retryProperties().attempts(),
            applicationConfig.retryProperties().backoff()
        ).filter(throwable -> getStatusCodes(throwable, statusCodes));
    }

    private boolean getStatusCodes(Throwable throwable, List<Integer> statusCodes) {
        if (throwable instanceof WebClientResponseException) {
            int statusCode = ((WebClientResponseException) throwable).getStatusCode().value();
            return statusCodes.contains(statusCode);
        }
        return false;
    }
}

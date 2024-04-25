package edu.java.configuration.retry;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

@Component
public abstract class RetryBuilder {
    public abstract Retry build(Integer attempts, Duration backoff, List<Integer> statusCodes);

    public abstract RetryPolicy.BackoffStrategy backoffStrategy();

    public Predicate<Throwable> filterExceptionsShouldBeRetried(@NotNull List<Integer> statusCodes) {
        return throwable -> {
            if (throwable instanceof WebClientResponseException) {
                int statusCode = ((WebClientResponseException) throwable).getStatusCode().value();
                return statusCodes.contains(statusCode);
            }
            return false;
        };
    }
}

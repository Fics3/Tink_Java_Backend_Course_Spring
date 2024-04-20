package edu.java.configuration.retry;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.function.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;
import static edu.java.configuration.retry.ConstantRetry.constantRetry;
import static edu.java.configuration.retry.ExponentRetry.exponentRetry;
import static edu.java.configuration.retry.LinearRetry.linearRetry;

@UtilityClass
public class RetryUtils {

    public static Predicate<Throwable> createFilterExceptionsShouldBeRetried(@NotNull List<Integer> statusCodes) {
        return throwable -> {
            if (throwable instanceof WebClientResponseException) {
                int statusCode = ((WebClientResponseException) throwable).getStatusCode().value();
                return statusCodes.contains(statusCode);
            }
            return false;
        };
    }

    public static Retry createRetry(RetryPolicy retryPolicy) {
        return switch (retryPolicy.backoffStrategy) {
            case constant -> constantRetry(retryPolicy.retryStatusCodes, retryPolicy.attempts, retryPolicy.backoff);
            case linear -> linearRetry(retryPolicy.retryStatusCodes, retryPolicy.attempts, retryPolicy.backoff);
            case exponent -> exponentRetry(retryPolicy.retryStatusCodes, retryPolicy.attempts, retryPolicy.backoff);
        };
    }
}

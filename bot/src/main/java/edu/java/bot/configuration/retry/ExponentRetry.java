package edu.java.bot.configuration.retry;

import java.time.Duration;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.util.retry.Retry;

@Log4j2
@Component

public class ExponentRetry extends RetryBuilder {

    @Override
    public Retry build(Integer attempts, Duration backoff, List<Integer> statusCodes) {
        return Retry.backoff(
            attempts,
            backoff
        ).filter(filterExceptionsShouldBeRetried(statusCodes));
    }

    @Override
    public RetryPolicy.BackoffStrategy backoffStrategy() {
        return RetryPolicy.BackoffStrategy.exponent;
    }
}

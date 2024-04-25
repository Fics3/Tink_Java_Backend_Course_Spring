package edu.java.configuration.retry;

import java.time.Duration;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.util.retry.Retry;

@Log4j2
@Component
public class ConstantRetry extends RetryBuilder {
    @Override
    public Retry build(Integer attempts, Duration backoff, List<Integer> statusCodes) {
        return Retry.fixedDelay(
                attempts,
                backoff
            ).filter(filterExceptionsShouldBeRetried(statusCodes))
            .doBeforeRetry(x -> log.info("ПОВТОР!!"));
    }

    @Override
    public RetryPolicy.BackoffStrategy backoffStrategy() {
        return RetryPolicy.BackoffStrategy.constant;
    }
}

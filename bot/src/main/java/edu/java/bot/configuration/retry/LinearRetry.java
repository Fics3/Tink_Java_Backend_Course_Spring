package edu.java.bot.configuration.retry;

import edu.java.bot.exception.RateLimitBotException;
import java.time.Duration;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

@Log4j2
@Component
public class LinearRetry extends RetryBuilder {

    @Override
    public Retry build(Integer attempts, Duration backoff, List<Integer> statusCodes) {
        var filter = filterExceptionsShouldBeRetried(statusCodes);
        return Retry.from(
            retrySignalFlux ->
                Flux.deferContextual(contextView ->
                    retrySignalFlux.contextWrite(contextView)
                        .concatMap(retrySignal -> {
                            Retry.RetrySignal copy = retrySignal.copy();
                            Throwable currentFailure = copy.failure();
                            long iteration = copy.totalRetries();
                            if (currentFailure == null) {
                                return Mono.error(
                                    new IllegalStateException("Retry вызван не ошибкой")
                                );
                            }

                            if (!filter.test(currentFailure)) {
                                return Mono.error(currentFailure);
                            }

                            if (iteration >= attempts) {
                                return Mono.error(() -> new RateLimitBotException(
                                    "Превышено количество ошибок",
                                    "Linear backoff"
                                ));
                            }

                            Duration nextBackoff = backoff.multipliedBy(iteration);
                            return Mono.delay(nextBackoff, Schedulers.parallel());
                        })
                )
        );
    }

    @Override
    public RetryPolicy.BackoffStrategy backoffStrategy() {
        return RetryPolicy.BackoffStrategy.linear;
    }
}

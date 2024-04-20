package edu.java.bot.configuration.retry;

import edu.java.bot.exception.RateLimitBotException;
import java.time.Duration;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;
import static edu.java.bot.configuration.retry.RetryUtils.createFilterWithCodes;

@Log4j2
@UtilityClass
public class LinearRetry {
    public static Retry linearRetry(List<Integer> statusCodes, Integer attempts, Duration backoff) {
        var filter = createFilterWithCodes(statusCodes);
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
                                return Mono.error(() -> new RateLimitBotException("Превышено количество ошибок",
                                    "Linear backoff"));
                            }

                            Duration nextBackoff = backoff.multipliedBy(iteration);
                            return Mono.delay(nextBackoff, Schedulers.parallel());
                        })
                )
        );
    }
}

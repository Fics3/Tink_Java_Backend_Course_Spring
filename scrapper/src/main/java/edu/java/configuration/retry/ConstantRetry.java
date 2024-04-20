package edu.java.configuration.retry;

import java.time.Duration;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import reactor.util.retry.Retry;
import static edu.java.configuration.retry.RetryUtils.createFilterExceptionsShouldBeRetried;

@Log4j2
@UtilityClass
public class ConstantRetry {
    public static Retry constantRetry(List<Integer> statusCodes, Integer attempts, Duration backoff) {
        return Retry.fixedDelay(
                attempts,
                backoff
            ).filter(createFilterExceptionsShouldBeRetried(statusCodes))
            .doBeforeRetry(x -> log.info("ПОВТОР!!"));
    }

}

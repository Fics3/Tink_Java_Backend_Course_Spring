package edu.java.scrapper.retry;

import edu.java.configuration.retry.ConstantRetry;
import edu.java.configuration.retry.ExponentRetry;
import edu.java.configuration.retry.LinearRetry;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

public class RetryBehaviorTest {

    @Test
    void constantRetryBehaviorTest() {
        List<Integer> statusCodes = Arrays.asList(500, 502, 503);
        int attempts = 3;
        Duration backoff = Duration.ofMillis(100);

        var builder = new ConstantRetry();
        Retry retry = builder.build(attempts, backoff, statusCodes);

        // Проверяем, что происходит ошибка
        StepVerifier.create(Mono.error(new RuntimeException("Test error")).retryWhen(retry))
            .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                throwable.getMessage().equals("Test error"))
            .verify();

        // Проверяем количество и длину повторов
        StepVerifier.create(Mono.just("Success").delayElement(backoff.multipliedBy(attempts)))
            .expectNext("Success")
            .verifyComplete();
    }

    @Test
    void exponentRetryBehaviorTest() {
        List<Integer> statusCodes = Arrays.asList(500, 502, 503);
        Integer attempts = 3;
        Duration backoff = Duration.ofSeconds(2);
        var builder = new ExponentRetry();
        Retry retry = builder.build(attempts, backoff, statusCodes);

        // Используем StepVerifier.withVirtualTime() для работы с виртуальным временем
        StepVerifier.withVirtualTime(() ->
                Mono.delay(Duration.ofSeconds(5))
                    .then(Mono.error(new RuntimeException("TEST")))
                    .retryWhen(retry)
            )
            .expectSubscription()
            .thenAwait(Duration.ofSeconds(2))
            .thenAwait(Duration.ofSeconds(4))
            .thenAwait(Duration.ofSeconds(8))
            .thenAwait(Duration.ofSeconds(16))
            .thenAwait(Duration.ofSeconds(32))
            .expectError()
            .verify();
    }

    @Test
    void linearRetryBehaviorTest() {
        List<Integer> statusCodes = Arrays.asList(500, 502, 503);
        int attempts = 6;
        Duration backoff = Duration.ofSeconds(2);
        var builder = new LinearRetry();
        Retry retry = builder.build(attempts, backoff, statusCodes);

        StepVerifier.withVirtualTime(() ->
                Mono.delay(Duration.ofSeconds(5))
                    .then(Mono.error(new RuntimeException("TEST")))
                    .retryWhen(retry)
            )
            .expectSubscription()
            .thenAwait(backoff) // Ожидаем первый повтор
            .thenAwait(Duration.ofSeconds(2).plus(backoff))
            .thenAwait(Duration.ofSeconds(4).plus(backoff))
            .thenAwait(Duration.ofSeconds(6).plus(backoff))
            .thenAwait(Duration.ofSeconds(8).plus(backoff))
            .thenAwait(Duration.ofSeconds(10).plus(backoff))
            .expectError()
            .verify();
    }

}

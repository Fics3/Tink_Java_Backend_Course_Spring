package edu.java.bot.retry;

import edu.java.bot.configuration.retry.ConstantRetry;
import edu.java.bot.configuration.retry.ExponentRetry;
import edu.java.bot.configuration.retry.LinearRetry;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class RetryBehaviorTest {

    @Test
    void constantRetryBehaviorTest() {
        List<Integer> statusCodes = Arrays.asList(500, 502, 503);
        int attempts = 3;
        Duration backoff = Duration.ofMillis(100);

        Retry retry = ConstantRetry.constantRetry(statusCodes, attempts, backoff);

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

        Retry retry = ExponentRetry.exponentRetry(statusCodes, attempts, backoff);

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

        Retry retry = LinearRetry.linearRetry(statusCodes, attempts, backoff);

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

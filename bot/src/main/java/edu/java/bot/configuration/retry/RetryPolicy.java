package edu.java.bot.configuration.retry;

import java.time.Duration;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Для расширения это не record, чтобы можно было наследоваться и получать класс с базовыми параметрами
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RetryPolicy {

    protected Integer attempts;
    protected Duration backoff;
    protected BackoffStrategy backoffStrategy;
    protected List<Integer> retryStatusCodes;

    public enum BackoffStrategy {
        constant,
        linear,
        exponent

    }
}

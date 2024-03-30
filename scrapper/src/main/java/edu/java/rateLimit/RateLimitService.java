package edu.java.rateLimit;

import edu.java.configuration.ApplicationConfig;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateLimitService {
    private final ApplicationConfig applicationConfig;
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String apiKey) {
        return cache.computeIfAbsent(apiKey, this::newBucket);
    }

    private Bucket newBucket(String apiKey) {
        Bandwidth bandwidth = Bandwidth.classic(
            applicationConfig.rateLimitProperty().limit(),
            Refill.greedy(
                applicationConfig.rateLimitProperty().refillLimit(),
                applicationConfig.rateLimitProperty().delayRefill()
            )
        );
        return Bucket.builder()
            .addLimit(bandwidth)
            .build();
    }
}

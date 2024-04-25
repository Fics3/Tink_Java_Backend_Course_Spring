package edu.java.client;

import edu.java.configuration.ClientConfig;
import edu.java.configuration.retry.RetryBuilder;
import edu.java.configuration.retry.RetryPolicy;
import java.util.Map;
import org.example.dto.LinkUpdateRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
public class BotClient {

    private final WebClient botWebClient;
    private final ClientConfig clientConfig;
    @Qualifier("retryBuilder")
    private final Map<RetryPolicy.BackoffStrategy, RetryBuilder> retryBuilderMap;

    public BotClient(
        WebClient botWebClient,
        ClientConfig clientConfig,
        Map<RetryPolicy.BackoffStrategy, RetryBuilder> retryBuilderMap
    ) {
        this.botWebClient = botWebClient;
        this.clientConfig = clientConfig;
        this.retryBuilderMap = retryBuilderMap;
    }

    public Mono<Void> sendUpdate(LinkUpdateRequest linkUpdateRequest) {
        Retry retry = retryBuilderMap.get(clientConfig.botProperties().retryPolicy().getBackoffStrategy())
            .build(
                clientConfig.botProperties().retryPolicy().getAttempts(),
                clientConfig.botProperties().retryPolicy().getBackoff(),
                clientConfig.botProperties().retryPolicy().getRetryStatusCodes()
            );
        return botWebClient
            .post()
            .uri("/updates")
            .body(Mono.just(linkUpdateRequest), LinkUpdateRequest.class)
            .retrieve()
            .bodyToMono(Void.class)
            .retryWhen(retry);
    }
}

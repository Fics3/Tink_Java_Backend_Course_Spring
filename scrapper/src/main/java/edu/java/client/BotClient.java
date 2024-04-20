package edu.java.client;

import edu.java.configuration.ClientConfig;
import lombok.RequiredArgsConstructor;
import org.example.dto.LinkUpdateRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import static edu.java.configuration.retry.RetryUtils.createRetry;

@Component
@RequiredArgsConstructor
public class BotClient {

    private final WebClient botWebClient;
    private final ClientConfig clientConfig;

    public Mono<Void> sendUpdate(LinkUpdateRequest linkUpdateRequest) {
        Retry retry = createRetry(clientConfig.botProperties().retryPolicy());
        return botWebClient
            .post()
            .uri("/updates")
            .body(Mono.just(linkUpdateRequest), LinkUpdateRequest.class)
            .retrieve()
            .bodyToMono(Void.class)
            .retryWhen(retry);
    }
}

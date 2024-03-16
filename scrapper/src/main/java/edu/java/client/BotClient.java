package edu.java.client;

import lombok.RequiredArgsConstructor;
import org.example.dto.LinkUpdateRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BotClient {

    private final WebClient botWebClient;

    public Mono<Void> sendUpdate(LinkUpdateRequest linkUpdateRequest) {
        return botWebClient
            .post()
            .uri("/updates")
            .body(Mono.just(linkUpdateRequest), LinkUpdateRequest.class)
            .retrieve()
            .bodyToMono(Void.class);
    }
}

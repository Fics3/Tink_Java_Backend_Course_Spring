package edu.java.client;

import lombok.AllArgsConstructor;
import org.example.dto.LinkUpdateRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
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

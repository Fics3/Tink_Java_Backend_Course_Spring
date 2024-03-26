package edu.java.client;

import org.example.dto.LinkUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class BotClient {

    private final WebClient botClient;

    public BotClient(WebClient botWebClient) {
        this.botClient = botWebClient;
    }

    public Mono<Void> sendUpdate(LinkUpdateRequest linkUpdateRequest) {
        return botClient
            .post()
            .uri("/updates")
            .body(Mono.just(linkUpdateRequest), LinkUpdateRequest.class)
            .retrieve()
            .bodyToMono(Void.class);
    }
}

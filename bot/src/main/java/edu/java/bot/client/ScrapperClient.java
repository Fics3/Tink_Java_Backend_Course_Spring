package edu.java.bot.client;

import edu.java.bot.configuration.ApplicationConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.dto.AddLinkRequest;
import org.example.dto.LinkResponse;
import org.example.dto.ListLinkResponse;
import org.example.dto.RemoveLinkRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
@Getter
@RequiredArgsConstructor
public class ScrapperClient {
    private final WebClient scrapperWebClient;
    private final ApplicationConfig applicationConfig;
    private final Retry retry;

    public Mono<String> registerChat(Long chatId) {
        return scrapperWebClient
            .post()
            .uri(applicationConfig.scrapperProperties().chat(), chatId)
            .retrieve()
            .bodyToMono(String.class)
            .retryWhen(retry);
    }

    public Mono<String> deleteChat(Long chatId) {
        return scrapperWebClient
            .delete()
            .uri(applicationConfig.scrapperProperties().chat(), chatId)
            .retrieve()
            .bodyToMono(String.class)
            .retryWhen(retry);
    }

    public Mono<ListLinkResponse> getAllLinks(Long tgChatId) {
        return scrapperWebClient
            .get()
            .uri(applicationConfig.scrapperProperties().links())
            .header(applicationConfig.scrapperProperties().tgChatId(), String.valueOf(tgChatId))
            .retrieve()
            .bodyToMono(ListLinkResponse.class)
            .retryWhen(retry);
    }

    public ResponseEntity<LinkResponse> addLink(Long tgChatId, AddLinkRequest addLinkRequest) {
        return scrapperWebClient
            .post()
            .uri(applicationConfig.scrapperProperties().links())
            .header(applicationConfig.scrapperProperties().tgChatId(), String.valueOf(tgChatId))
            .body(Mono.just(addLinkRequest), AddLinkRequest.class)
            .retrieve()
            .toEntity(LinkResponse.class)
            .onErrorResume(WebClientResponseException.class, Mono::error)
            .retryWhen(retry)
            .block();
    }

    public ResponseEntity<LinkResponse> removeLink(Long tgChatId, RemoveLinkRequest removeLinkRequest) {
        return scrapperWebClient
            .method(HttpMethod.DELETE)
            .uri(applicationConfig.scrapperProperties().links())
            .header(applicationConfig.scrapperProperties().tgChatId(), String.valueOf(tgChatId))
            .body(Mono.just(removeLinkRequest), RemoveLinkRequest.class)
            .retrieve()
            .toEntity(LinkResponse.class)
            .onErrorResume(WebClientResponseException.class, Mono::error)
            .retryWhen(retry)
            .block();
    }
}

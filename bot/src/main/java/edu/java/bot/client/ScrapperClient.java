package edu.java.bot.client;

import edu.java.bot.configuration.ClientConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.dto.AddLinkRequest;
import org.example.dto.LinkResponse;
import org.example.dto.ListLinkResponse;
import org.example.dto.RemoveLinkRequest;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import static edu.java.bot.configuration.retry.RetryUtils.createRetry;

@Component
@Getter
@RequiredArgsConstructor
public class ScrapperClient {
    private final WebClient scrapperWebClient;
    private final ClientConfig clientConfig;

    public Mono<String> registerChat(Long chatId) {
        var retry = createRetry(clientConfig.scrapperProperties().retryPolicy());
        return scrapperWebClient
            .post()
            .uri(clientConfig.scrapperProperties().chat(), chatId)
            .retrieve()
            .bodyToMono(String.class)
            .retryWhen(retry);
    }

    public Mono<String> deleteChat(Long chatId) {
        var retry = createRetry(clientConfig.scrapperProperties().retryPolicy());
        return scrapperWebClient
            .delete()
            .uri(clientConfig.scrapperProperties().chat(), chatId)
            .retrieve()
            .bodyToMono(String.class)
            .retryWhen(retry);
    }

    public Mono<ListLinkResponse> getAllLinks(Long tgChatId) {
        var retry = createRetry(clientConfig.scrapperProperties().retryPolicy());
        return scrapperWebClient
            .get()
            .uri(clientConfig.scrapperProperties().links())
            .header(clientConfig.scrapperProperties().tgChatId(), String.valueOf(tgChatId))
            .retrieve()
            .bodyToMono(ListLinkResponse.class)
            .retryWhen(retry);
    }

    public void addLink(Long tgChatId, AddLinkRequest addLinkRequest) {
        var retry = createRetry(clientConfig.scrapperProperties().retryPolicy());
        scrapperWebClient
            .post()
            .uri(clientConfig.scrapperProperties().links())
            .header(clientConfig.scrapperProperties().tgChatId(), String.valueOf(tgChatId))
            .body(Mono.just(addLinkRequest), AddLinkRequest.class)
            .retrieve()
            .toEntity(LinkResponse.class)
            .onErrorResume(WebClientResponseException.class, Mono::error)
            .retryWhen(retry)
            .block();
    }

    public void removeLink(Long tgChatId, RemoveLinkRequest removeLinkRequest) {
        var retry = createRetry(clientConfig.scrapperProperties().retryPolicy());
        scrapperWebClient
            .method(HttpMethod.DELETE)
            .uri(clientConfig.scrapperProperties().links())
            .header(clientConfig.scrapperProperties().tgChatId(), String.valueOf(tgChatId))
            .body(Mono.just(removeLinkRequest), RemoveLinkRequest.class)
            .retrieve()
            .toEntity(LinkResponse.class)
            .onErrorResume(WebClientResponseException.class, Mono::error)
            .retryWhen(retry)
            .block();
    }
}

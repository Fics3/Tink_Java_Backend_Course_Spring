package edu.java.bot.client;

import edu.java.bot.configuration.ClientConfig;
import edu.java.bot.configuration.retry.RetryBuilder;
import edu.java.bot.configuration.retry.RetryPolicy;
import java.util.Map;
import lombok.Getter;
import org.example.dto.AddLinkRequest;
import org.example.dto.LinkResponse;
import org.example.dto.ListLinkResponse;
import org.example.dto.RemoveLinkRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@Getter
public class ScrapperClient {
    private final WebClient scrapperWebClient;
    private final ClientConfig clientConfig;
    @Qualifier("retryBuilder")
    private final Map<RetryPolicy.BackoffStrategy, RetryBuilder> retryBuilderMap;

    public ScrapperClient(
        WebClient scrapperWebClient,
        ClientConfig clientConfig,
        Map<RetryPolicy.BackoffStrategy, RetryBuilder> retryBuilderMap
    ) {
        this.scrapperWebClient = scrapperWebClient;
        this.clientConfig = clientConfig;
        this.retryBuilderMap = retryBuilderMap;
    }

    public Mono<String> registerChat(Long chatId) {
        var retry = retryBuilderMap.get(clientConfig.scrapperProperties().retryPolicy().getBackoffStrategy())
            .build(
                clientConfig.scrapperProperties().retryPolicy().getAttempts(),
                clientConfig.scrapperProperties().retryPolicy().getBackoff(),
                clientConfig.scrapperProperties().retryPolicy().getRetryStatusCodes()
            );
        return scrapperWebClient
            .post()
            .uri(clientConfig.scrapperProperties().chat(), chatId)
            .retrieve()
            .bodyToMono(String.class)
            .retryWhen(retry);
    }

    public Mono<String> deleteChat(Long chatId) {
        var retry = retryBuilderMap.get(clientConfig.scrapperProperties().retryPolicy().getBackoffStrategy())
            .build(
                clientConfig.scrapperProperties().retryPolicy().getAttempts(),
                clientConfig.scrapperProperties().retryPolicy().getBackoff(),
                clientConfig.scrapperProperties().retryPolicy().getRetryStatusCodes()
            );
        return scrapperWebClient
            .delete()
            .uri(clientConfig.scrapperProperties().chat(), chatId)
            .retrieve()
            .bodyToMono(String.class)
            .retryWhen(retry);
    }

    public Mono<ListLinkResponse> getAllLinks(Long tgChatId) {
        var retry = retryBuilderMap.get(clientConfig.scrapperProperties().retryPolicy().getBackoffStrategy())
            .build(
                clientConfig.scrapperProperties().retryPolicy().getAttempts(),
                clientConfig.scrapperProperties().retryPolicy().getBackoff(),
                clientConfig.scrapperProperties().retryPolicy().getRetryStatusCodes()
            );
        return scrapperWebClient
            .get()
            .uri(clientConfig.scrapperProperties().links())
            .header(clientConfig.scrapperProperties().tgChatId(), String.valueOf(tgChatId))
            .retrieve()
            .bodyToMono(ListLinkResponse.class)
            .retryWhen(retry);
    }

    public void addLink(Long tgChatId, AddLinkRequest addLinkRequest) {
        var retry = retryBuilderMap.get(clientConfig.scrapperProperties().retryPolicy().getBackoffStrategy())
            .build(
                clientConfig.scrapperProperties().retryPolicy().getAttempts(),
                clientConfig.scrapperProperties().retryPolicy().getBackoff(),
                clientConfig.scrapperProperties().retryPolicy().getRetryStatusCodes()
            );
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
        var retry = retryBuilderMap.get(clientConfig.scrapperProperties().retryPolicy().getBackoffStrategy())
            .build(
                clientConfig.scrapperProperties().retryPolicy().getAttempts(),
                clientConfig.scrapperProperties().retryPolicy().getBackoff(),
                clientConfig.scrapperProperties().retryPolicy().getRetryStatusCodes()
            );
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

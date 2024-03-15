package edu.java.bot.client;

import lombok.Getter;
import org.example.dto.AddLinkRequest;
import org.example.dto.LinkResponse;
import org.example.dto.ListLinkResponse;
import org.example.dto.RemoveLinkRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Getter
public class ScrapperClient {
    private final WebClient scrapperWebClient;
    @Value(value = "${app.scrapper-properties.chat}")
    private String chat;
    @Value(value = "${app.scrapper-properties.links}")
    private String links;
    @Value(value = "${app.scrapper-properties.tg-chat-id}")
    private String tgChatIdProperty;

    public ScrapperClient(WebClient scrapperWebClient) {
        this.scrapperWebClient = scrapperWebClient;
    }

    public Mono<Void> registerChat(Long chatId) {
        return scrapperWebClient
            .post()
            .uri(getChat(), chatId)
            .retrieve()
            .bodyToMono(Void.class);
    }

    public Mono<Void> deleteChat(Long chatId) {
        return scrapperWebClient
            .delete()
            .uri(getChat(), chatId)
            .retrieve()
            .bodyToMono(Void.class);
    }

    public Mono<ListLinkResponse> getAllLinks(Long tgChatId) {
        return scrapperWebClient
            .get()
            .uri(getLinks())
            .header(getTgChatIdProperty(), String.valueOf(tgChatId))
            .retrieve()
            .bodyToMono(ListLinkResponse.class);
    }

    public Mono<LinkResponse> addLink(Long tgChatId, AddLinkRequest addLinkRequest) {
        return scrapperWebClient
            .post()
            .uri(getLinks())
            .header(getTgChatIdProperty(), String.valueOf(tgChatId))
            .body(Mono.just(addLinkRequest), AddLinkRequest.class)
            .retrieve()
            .bodyToMono(LinkResponse.class);
    }

    public Mono<LinkResponse> removeLink(Long tgChatId, RemoveLinkRequest removeLinkRequest) {
        return scrapperWebClient
            .post()
            .uri(getLinks())
            .header(getTgChatIdProperty(), String.valueOf(tgChatId))
            .body(Mono.just(removeLinkRequest), RemoveLinkRequest.class)
            .retrieve()
            .bodyToMono(LinkResponse.class);
    }
}

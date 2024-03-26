package edu.java.client;

import org.example.dto.AddLinkRequest;
import org.example.dto.LinkResponse;
import org.example.dto.ListLinkResponse;
import org.example.dto.RemoveLinkRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ScrapperClient {

    private static final String CHAT = "/tg-chat/{id}";
    private static final String LINKS = "/links";
    private static final String TG_CHAT_ID = "Tg-Chat-Id";
    private final WebClient scrapperClient;

    public ScrapperClient(WebClient scrapperWebClient) {
        this.scrapperClient = scrapperWebClient;
    }

    public Mono<Void> registerChat(long chatId) {
        return scrapperClient
            .post()
            .uri(CHAT, chatId)
            .retrieve()
            .bodyToMono(Void.class);
    }

    public Mono<Void> deleteChat(long chatId) {
        return scrapperClient
            .delete()
            .uri(CHAT, chatId)
            .retrieve()
            .bodyToMono(Void.class);
    }

    public Mono<ListLinkResponse> getAllLinks(long tgChatId) {
        return scrapperClient
            .get()
            .uri(LINKS)
            .header(TG_CHAT_ID, String.valueOf(tgChatId))
            .retrieve()
            .bodyToMono(ListLinkResponse.class);
    }

    public Mono<LinkResponse> addLink(long tgChatId, AddLinkRequest addLinkRequest) {
        return scrapperClient
            .post()
            .uri(LINKS)
            .header(TG_CHAT_ID, String.valueOf(tgChatId))
            .body(Mono.just(addLinkRequest), AddLinkRequest.class)
            .retrieve()
            .bodyToMono(LinkResponse.class);
    }

    public Mono<LinkResponse> removeLink(long tgChatId, RemoveLinkRequest removeLinkRequest) {
        return scrapperClient
            .post()
            .uri(LINKS)
            .header(TG_CHAT_ID, String.valueOf(tgChatId))
            .body(Mono.just(removeLinkRequest), RemoveLinkRequest.class)
            .retrieve()
            .bodyToMono(LinkResponse.class);
    }
}

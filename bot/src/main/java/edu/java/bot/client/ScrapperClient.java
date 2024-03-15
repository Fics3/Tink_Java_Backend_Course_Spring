package edu.java.bot.client;

import edu.java.bot.configuration.ApplicationConfig;
import lombok.AllArgsConstructor;
import org.example.dto.AddLinkRequest;
import org.example.dto.LinkResponse;
import org.example.dto.ListLinkResponse;
import org.example.dto.RemoveLinkRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class ScrapperClient {
    private final ApplicationConfig applicationConfig;
    private final WebClient scrapperWebClient;

    public Mono<Void> registerChat(long chatId) {
        return scrapperWebClient
            .post()
            .uri(applicationConfig.scrapperProperties().chat(), chatId)
            .retrieve()
            .bodyToMono(Void.class);
    }

    public Mono<Void> deleteChat(long chatId) {
        return scrapperWebClient
            .delete()
            .uri(applicationConfig.scrapperProperties().chat(), chatId)
            .retrieve()
            .bodyToMono(Void.class);
    }

    public Mono<ListLinkResponse> getAllLinks(long tgChatId) {
        return scrapperWebClient
            .get()
            .uri(applicationConfig.scrapperProperties().links())
            .header(applicationConfig.scrapperProperties().tgChatId(), String.valueOf(tgChatId))
            .retrieve()
            .bodyToMono(ListLinkResponse.class);
    }

    public Mono<LinkResponse> addLink(long tgChatId, AddLinkRequest addLinkRequest) {
        return scrapperWebClient
            .post()
            .uri(applicationConfig.scrapperProperties().links())
            .header(applicationConfig.scrapperProperties().tgChatId(), String.valueOf(tgChatId))
            .body(Mono.just(addLinkRequest), AddLinkRequest.class)
            .retrieve()
            .bodyToMono(LinkResponse.class);
    }

    public Mono<LinkResponse> removeLink(long tgChatId, RemoveLinkRequest removeLinkRequest) {
        return scrapperWebClient
            .post()
            .uri(applicationConfig.scrapperProperties().links())
            .header(applicationConfig.scrapperProperties().tgChatId(), String.valueOf(tgChatId))
            .body(Mono.just(removeLinkRequest), RemoveLinkRequest.class)
            .retrieve()
            .bodyToMono(LinkResponse.class);
    }
}

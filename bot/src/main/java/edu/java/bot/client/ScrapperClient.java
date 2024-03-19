package edu.java.bot.client;

import edu.java.bot.configuration.ApplicationConfig;
import lombok.Getter;
import org.example.dto.AddLinkRequest;
import org.example.dto.LinkResponse;
import org.example.dto.ListLinkResponse;
import org.example.dto.RemoveLinkRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Getter
public class ScrapperClient {
    private final WebClient scrapperWebClient;
    private final ApplicationConfig applicationConfig;

    public ScrapperClient(WebClient scrapperWebClient, ApplicationConfig applicationConfig) {
        this.scrapperWebClient = scrapperWebClient;
        this.applicationConfig = applicationConfig;
    }

    public Mono<String> registerChat(Long chatId) {
        return scrapperWebClient
            .post()
            .uri(applicationConfig.scrapperProperties().chat(), chatId)
            .retrieve()
            .bodyToMono(String.class);
    }

    public Mono<String> deleteChat(Long chatId) {
        return scrapperWebClient
            .delete()
            .uri(applicationConfig.scrapperProperties().chat(), chatId)
            .retrieve()
            .bodyToMono(String.class);
    }

    public Mono<ListLinkResponse> getAllLinks(Long tgChatId) {
        return scrapperWebClient
            .get()
            .uri(applicationConfig.scrapperProperties().links())
            .header(applicationConfig.scrapperProperties().tgChatId(), String.valueOf(tgChatId))
            .retrieve()
            .bodyToMono(ListLinkResponse.class);
    }

    public Mono<LinkResponse> addLink(Long tgChatId, AddLinkRequest addLinkRequest) {
        return scrapperWebClient
            .post()
            .uri(applicationConfig.scrapperProperties().links())
            .header(applicationConfig.scrapperProperties().tgChatId(), String.valueOf(tgChatId))
            .body(Mono.just(addLinkRequest), AddLinkRequest.class)
            .retrieve()
            .bodyToMono(LinkResponse.class);
    }

    public Mono<LinkResponse> removeLink(Long tgChatId, RemoveLinkRequest removeLinkRequest) {
        return scrapperWebClient
            .post()
            .uri(applicationConfig.scrapperProperties().links())
            .header(applicationConfig.scrapperProperties().tgChatId(), String.valueOf(tgChatId))
            .body(Mono.just(removeLinkRequest), RemoveLinkRequest.class)
            .retrieve()
            .bodyToMono(LinkResponse.class);
    }
}

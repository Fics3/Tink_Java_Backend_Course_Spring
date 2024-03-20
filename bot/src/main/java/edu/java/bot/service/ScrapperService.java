package edu.java.bot.service;

import edu.java.bot.client.ScrapperClient;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.example.dto.AddLinkRequest;
import org.example.dto.ListLinkResponse;
import org.example.dto.RemoveLinkRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScrapperService {

    private final ScrapperClient scrapperClient;

    public void registerChat(Long tgChatId) {
        scrapperClient.registerChat(tgChatId).block();
    }

    public void deleteChat(Long thChatId) {
        scrapperClient.deleteChat(thChatId).block();
    }

    public ListLinkResponse getAllLinks(Long tgChatId) {
        return scrapperClient.getAllLinks(tgChatId).block();
    }

    public void addLink(Long tgChatId, URI url) {
        scrapperClient.addLink(tgChatId, new AddLinkRequest(url));
    }

    public void deleteLink(Long tgChatId, URI url) {
        scrapperClient.removeLink(tgChatId, new RemoveLinkRequest(url));
    }

}

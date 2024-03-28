package edu.java.service;

import edu.java.domain.repository.LinksRepository;
import edu.java.exception.DuplicateLinkScrapperException;
import edu.java.model.LinkModel;
import edu.java.service.linkAdder.LinkAdder;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.example.dto.LinkResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class LinkService {
    private final Map<String, LinkAdder> linkAdders;
    private final LinksRepository jooqLinksRepository;

    public LinkService(
        @Qualifier("linkAdders") Map<String, LinkAdder> linkAdders,
        LinksRepository jooqLinksRepository
    ) {
        this.linkAdders = linkAdders;
        this.jooqLinksRepository = jooqLinksRepository;
    }

    public LinkModel add(Long tgChatId, URI url) {
        if (jooqLinksRepository.existsLinkForChat(tgChatId, url.toString())) {
            throw new DuplicateLinkScrapperException("Ссылка уже существует", url + "уже отсвеживается");
        }
        return linkAdders.get(url.getHost()).addLink(url, tgChatId);
    }

    public LinkModel remove(Long tgChatId, URI url) {
        return jooqLinksRepository.removeLink(tgChatId, url.toString());
    }

    public List<LinkResponse> findAll(Long tgChatId) {
        return jooqLinksRepository.findLinksByChatId(tgChatId).stream()
            .map(linkModel -> new LinkResponse(
                URI.create(linkModel.link()),
                linkModel.lastUpdate()
            ))
            .collect(Collectors.toList());
    }
}

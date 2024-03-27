package edu.java.service;

import edu.java.exception.DuplicateLinkScrapperException;
import edu.java.model.LinkModel;
import edu.java.repository.LinksRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.example.dto.LinkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinkService {

    @Autowired
    private LinksRepository linksRepository;

    public LinkModel add(Long tgChatId, URI url) {
        if (linksRepository.existsLinkForChat(tgChatId, url.toString())) {
            throw new DuplicateLinkScrapperException("Ссылка уже существует", url + "уже отсвеживается");
        }
        return linksRepository.addLink(tgChatId, url.toString(), OffsetDateTime.now());
    }

    public LinkModel remove(Long tgChatId, URI url) {
        return linksRepository.removeLink(tgChatId, url.toString());
    }

    public List<LinkResponse> findAll(Long tgChatId) {
        return linksRepository.findAllLinks().stream()
            .map(linkModel -> new LinkResponse(
                URI.create(linkModel.link()),
                linkModel.lastUpdate()
            ))
            .collect(Collectors.toList());
    }
}

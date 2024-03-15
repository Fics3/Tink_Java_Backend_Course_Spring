package edu.java.jdbc;

import edu.java.exception.DuplicateLinkScrapperException;
import edu.java.model.LinkModel;
import edu.java.repository.LinksRepository;
import edu.java.service.LinkService;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.example.dto.LinkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JdbcLinkService implements LinkService {

    @Autowired
    private LinksRepository linksRepository;

    @Override
    public LinkModel add(Long tgChatId, URI url) {
        if (linksRepository.existsLinkForChat(tgChatId, url.toString())) {
            throw new DuplicateLinkScrapperException("Ссылка уже существует", url + "уже отсвеживается");
        }
        return linksRepository.addLink(tgChatId, url.toString());
    }

    @Override
    public LinkModel remove(Long tgChatId, URI url) {
        return linksRepository.removeLink(tgChatId, url.toString());
    }

    @Override
    public List<LinkResponse> findAll(Long tgChatId) {
        return linksRepository.findAllLinks().stream()
            .map(linkModel -> new LinkResponse(
                URI.create(linkModel.link()),
                linkModel.lastUpdate()
            ))
            .collect(Collectors.toList());
    }
}

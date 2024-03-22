package edu.java.service.jpa;

import edu.java.client.GithubClient;
import edu.java.client.StackoverflowClient;
import edu.java.domain.entity.ChatEntity;
import edu.java.domain.entity.LinkEntity;
import edu.java.domain.repository.jpa.JpaChatRepository;
import edu.java.domain.repository.jpa.JpaLinksRepository;
import edu.java.exception.BadRequestScrapperException;
import edu.java.exception.DuplicateLinkScrapperException;
import edu.java.model.LinkModel;
import edu.java.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.dto.LinkResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaLinkService implements LinkService {
    private final GithubClient githubClient;
    private final StackoverflowClient stackoverflowClient;
    private final JpaLinksRepository jpaLinksRepository;
    private final JpaChatRepository jpaChatRepository;

    @Override
    public LinkModel add(Long tgChatId, URI url) {
        jpaChatRepository.findByTelegramChatId(tgChatId)
            .orElseThrow(() -> new DuplicateLinkScrapperException("Ссылка уже существует", url + " уже отслеживается"));

        OffsetDateTime now = OffsetDateTime.now();
        switch (url.getHost()) {
            case "stackoverflow.com" -> {
                var question = stackoverflowClient.fetchQuestion(url).block();
                return saveLink(
                    tgChatId,
                    url,
                    Objects.requireNonNull(question).items().getFirst().lastActivityDate(),
                    now
                );
            }
            case "github.com" -> {
                var repository = githubClient.fetchRepository(url).block();
                return saveLink(tgChatId, url, Objects.requireNonNull(repository).pushedAt(), now);
            }
            default -> {
                return saveLink(tgChatId, url, now, now);
            }
        }
    }

    @Override
    public LinkModel remove(Long tgChatId, URI url) {
        ChatEntity chatEntity = getChatByTgChatId(tgChatId);
        LinkEntity linkEntity = jpaLinksRepository.findLinkEntityByLink(url.toString())
            .orElseThrow(() -> new BadRequestScrapperException("Такой ссылки не существует", ""));
        chatEntity.getLinks().remove(linkEntity);
        if (chatEntity.getLinks().isEmpty()) {
            jpaLinksRepository.delete(linkEntity);
        }

        return new LinkModel(
            linkEntity.getLinkId(),
            linkEntity.getLink(),
            linkEntity.getLastUpdate(),
            linkEntity.getLastCheck()
        );
    }

    @Override
    public List<LinkResponse> findAll(Long tgChatId) {
        var chatEntities = getChatByTgChatId(tgChatId);
        return chatEntities
            .getLinks()
            .stream()
            .map(linkModel -> new LinkResponse(
                URI.create(linkModel.getLink()),
                linkModel.getLastUpdate()
            ))
            .collect(Collectors.toList());
    }

    private LinkModel saveLink(Long tgChatId, URI url, OffsetDateTime lastUpdate, OffsetDateTime now) {
        var chatEntity = getChatByTgChatId(tgChatId);
        var linkEntity = jpaLinksRepository.findLinkEntityByLink(url.toString()).orElse(
            new LinkEntity(chatEntity, UUID.randomUUID(), url.toString(), lastUpdate, now)
        );

        jpaLinksRepository.save(linkEntity);

        return new LinkModel(
            linkEntity.getLinkId(),
            linkEntity.getLink(),
            linkEntity.getLastUpdate(),
            linkEntity.getLastCheck()
        );
    }

    ChatEntity getChatByTgChatId(Long tgChatId) {
        return jpaChatRepository.findByTelegramChatId(tgChatId).orElseThrow(() -> new BadRequestScrapperException(
            "Пользователь не зарегистрирован",
            ""
        ));
    }
}

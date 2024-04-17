package edu.java.domain.repository.jpa;

import edu.java.domain.repository.LinksRepository;
import edu.java.domain.repository.jpa.entitiesRepository.JpaChatEntityRepository;
import edu.java.domain.repository.jpa.entitiesRepository.JpaLinkEntityRepository;
import edu.java.domain.repository.jpa.entity.ChatEntity;
import edu.java.domain.repository.jpa.entity.LinkEntity;
import edu.java.exception.BadRequestScrapperException;
import edu.java.exception.NotFoundScrapperException;
import edu.java.model.LinkModel;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class JpaLinksRepository implements LinksRepository {

    private static final String USER_NOT_REGISTERED = "Пользователь не зарегистрирован";
    private static final String ERROR = "Ошибка";
    private final JpaLinkEntityRepository jpaLinkEntityRepository;
    private final JpaChatEntityRepository jpaChatEntityRepository;

    @Override
    public LinkModel addLink(Long tgChatId, String link, OffsetDateTime lastUpdate) {
        var chat = jpaChatEntityRepository.findByTelegramChatId(tgChatId)
            .orElseThrow(() -> new NotFoundScrapperException(USER_NOT_REGISTERED, ERROR));
        var linkEntity = new LinkEntity(
            chat,
            UUID.randomUUID(),
            link,
            lastUpdate,
            OffsetDateTime.now()
        );
        chat.getLinks().add(linkEntity);

        jpaLinkEntityRepository.save(linkEntity);
        return new LinkModel(
            linkEntity.getLinkId(),
            linkEntity.getLink(),
            linkEntity.getLastUpdate(),
            linkEntity.getLastCheck()
        );

    }

    @Override
    public LinkModel removeLink(Long tgChatId, String link) {
        var linkEntity = jpaLinkEntityRepository.findLinkEntityByLink(link)
            .orElseThrow(() -> new BadRequestScrapperException("Ссылка не найдена", ERROR));

        for (ChatEntity chatEntity : linkEntity.getChats()) {
            chatEntity.getLinks().remove(linkEntity);
        }
        jpaLinkEntityRepository.delete(linkEntity);
        return new LinkModel(
            linkEntity.getLinkId(),
            linkEntity.getLink(),
            linkEntity.getLastUpdate(),
            linkEntity.getLastCheck()
        );
    }

    @Override
    public List<LinkModel> findAllLinks() {
        return jpaLinkEntityRepository.findAll().stream().map(
            linkEntity -> new LinkModel(
                linkEntity.getLinkId(),
                linkEntity.getLink(),
                linkEntity.getLastUpdate(),
                linkEntity.getLastCheck()
            )
        ).toList();
    }

    @Override
    public List<LinkModel> findLinksByChatId(Long tgChatId) {
        var chat = jpaChatEntityRepository.findByTelegramChatId(tgChatId)
            .orElseThrow(() -> new BadRequestScrapperException(USER_NOT_REGISTERED, ERROR));
        return jpaLinkEntityRepository.findAllByChatsContains(chat).stream().map(linkEntity -> new LinkModel(
            linkEntity.getLinkId(),
            linkEntity.getLink(),
            linkEntity.getLastUpdate(),
            linkEntity.getLastCheck()
        )).toList();
    }

    @Override
    public boolean existsLinkForChat(Long tgChatId, String url) {
        ChatEntity chat = jpaChatEntityRepository.findByTelegramChatId(tgChatId)
            .orElseThrow(() -> new BadRequestScrapperException(USER_NOT_REGISTERED, ERROR));
        return jpaLinkEntityRepository.existsByLinkAndChatEntities(url, Set.of(chat));
    }

    @Override
    public List<LinkModel> findStaleLinks(Duration threshold) {
        return jpaLinkEntityRepository
            .findByLastCheckAfter(OffsetDateTime.now().minus(threshold))
            .stream()
            .map(linkEntity -> new LinkModel(
                linkEntity.getLinkId(),
                linkEntity.getLink(),
                linkEntity.getLastUpdate(),
                linkEntity.getLastCheck()
            )).toList();
    }

    @Override
    public void updateLastUpdate(UUID linkId, OffsetDateTime lastUpdate) {
        jpaLinkEntityRepository.updateLastUpdate(linkId, lastUpdate);
    }

    @Override
    public void updateChecked(UUID linkId, OffsetDateTime checkedAt) {
        jpaLinkEntityRepository.updateChecked(linkId, checkedAt);
    }
}

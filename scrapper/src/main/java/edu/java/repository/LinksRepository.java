package edu.java.repository;

import edu.java.model.LinkModel;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface LinksRepository {

    LinkModel addLink(Long tgChatId, String link, OffsetDateTime lastUpdate);

    LinkModel removeLink(Long tgChatId, String link);

    List<LinkModel> findAllLinks();

    List<LinkModel> findLinksByChatId(Long tgChatId);

    boolean existsLinkForChat(Long tgChatId, String url);

    List<LinkModel> findStaleLinks(Duration threshold);

    void updateLastUpdate(UUID linkId, OffsetDateTime lastUpdate);

    void updateChecked(UUID linkId, OffsetDateTime checkedAt);

}

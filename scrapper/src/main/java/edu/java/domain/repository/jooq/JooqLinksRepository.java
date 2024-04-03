package edu.java.domain.repository.jooq;

import edu.java.domain.jooq.tables.records.LinksRecord;
import edu.java.domain.repository.LinksRepository;
import edu.java.exception.BadRequestScrapperException;
import edu.java.model.LinkModel;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.domain.jooq.Tables.CHAT_LINK_RELATION;
import static edu.java.domain.jooq.Tables.LINKS;

@Repository
@RequiredArgsConstructor
public class JooqLinksRepository implements LinksRepository {

    private final DSLContext dsl;
    private final JooqChatRepository jooqChatRepository;

    @Override
    public LinkModel addLink(Long tgChatId, String link, OffsetDateTime lastUpdate) {
        if (!jooqChatRepository.existsChat(tgChatId)) {
            throw new BadRequestScrapperException("Пользователь не зарегестрирован", "Зарегистрируйстесь");
        }

        UUID linkId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();

        dsl.insertInto(LINKS)
            .set(LINKS.LINK_ID, linkId)
            .set(LINKS.LINK, link)
            .set(LINKS.LAST_UPDATE, lastUpdate)
            .set(LINKS.LAST_CHECK, createdAt)
            .execute();

        dsl.insertInto(CHAT_LINK_RELATION)
            .set(CHAT_LINK_RELATION.CHAT_ID, tgChatId)
            .set(CHAT_LINK_RELATION.LINK_ID, linkId)
            .execute();

        return new LinkModel(linkId, link, lastUpdate, createdAt);
    }

    @Override
    public LinkModel removeLink(Long tgChatId, String link) {
        LinksRecord linkRecord = dsl.selectFrom(LINKS)
            .where(LINKS.LINK.eq(link))
            .fetchOne();

        if (linkRecord != null) {
            UUID linkId = linkRecord.getLinkId();

            dsl.deleteFrom(CHAT_LINK_RELATION)
                .where(CHAT_LINK_RELATION.LINK_ID.eq(linkId))
                .execute();

            dsl.deleteFrom(LINKS)
                .where(LINKS.LINK_ID.eq(linkId))
                .execute();

            return new LinkModel(linkId, link, null, null);
        }

        return null;
    }

    @Override
    public List<LinkModel> findAllLinks() {
        return dsl.selectFrom(LINKS)
            .fetch()
            .map(linksRecord -> new LinkModel(
                linksRecord.getLinkId(),
                linksRecord.getLink(),
                linksRecord.getLastUpdate(),
                linksRecord.getLastCheck()
            ));
    }

    @Override
    public List<LinkModel> findLinksByChatId(Long tgChatId) {
        return dsl.select(LINKS.LINK_ID, LINKS.LINK, LINKS.LAST_UPDATE, LINKS.LAST_CHECK)
            .from(CHAT_LINK_RELATION)
            .join(LINKS).on(CHAT_LINK_RELATION.LINK_ID.eq(LINKS.LINK_ID))
            .where(CHAT_LINK_RELATION.CHAT_ID.eq(tgChatId))
            .fetchInto(LinkModel.class);
    }

    @Override
    public boolean existsLinkForChat(Long tgChatId, String url) {
        return dsl.fetchExists(
            CHAT_LINK_RELATION.join(LINKS)
                .on(CHAT_LINK_RELATION.LINK_ID.eq(LINKS.LINK_ID))
                .where(CHAT_LINK_RELATION.CHAT_ID.eq(tgChatId)
                    .and(LINKS.LINK.eq(url)))
        );
    }

    @Override
    public List<LinkModel> findStaleLinks(Duration threshold) {
        OffsetDateTime staleThreshold = OffsetDateTime.now().minus(threshold);

        return dsl.selectFrom(LINKS)
            .where(LINKS.LAST_CHECK.lt(staleThreshold))
            .fetch()
            .map(linksRecord -> new LinkModel(
                linksRecord.getLinkId(),
                linksRecord.getLink(),
                linksRecord.getLastUpdate(),
                linksRecord.getLastCheck()
            ));
    }

    @Override
    public void updateLastUpdate(UUID linkId, OffsetDateTime lastUpdate) {
        dsl.update(LINKS)
            .set(LINKS.LAST_UPDATE, lastUpdate)
            .where(LINKS.LINK_ID.eq(linkId))
            .execute();
    }

    @Override
    public void updateChecked(UUID linkId, OffsetDateTime checkedAt) {
        dsl.update(LINKS)
            .set(LINKS.LAST_CHECK, checkedAt)
            .where(LINKS.LINK_ID.eq(linkId))
            .execute();
    }

}

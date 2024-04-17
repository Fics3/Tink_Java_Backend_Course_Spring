package edu.java.scrapper.repository.jooq;

import edu.java.domain.jooq.tables.records.LinksRecord;
import edu.java.domain.repository.jooq.JooqLinksRepository;
import edu.java.exception.NotFoundScrapperException;
import edu.java.model.LinkModel;
import edu.java.scrapper.IntegrationTest;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.domain.jooq.Tables.CHATS;
import static edu.java.domain.jooq.Tables.CHAT_LINK_RELATION;
import static edu.java.domain.jooq.Tables.LINKS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class JooqLinksRepositoryTest extends IntegrationTest {

    @Autowired
    private JooqLinksRepository jooqLinksRepository;

    @Autowired
    private DSLContext dslContext;

    @Test
    void addLinkTest() {
        // Arrange
        Long tgChatId = 123L;
        String link = "http://example.com";
        OffsetDateTime lastUpdate = OffsetDateTime.now();

        dslContext.insertInto(CHATS)
            .set(CHATS.TELEGRAM_CHAT_ID, tgChatId)
            .set(CHATS.CREATED_AT, OffsetDateTime.now())
            .execute();

        // Act
        LinkModel result = jooqLinksRepository.addLink(tgChatId, link, lastUpdate);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.linkId()).isNotNull();
        assertThat(result.link()).isEqualTo(link);
        assertThat(result.lastUpdate()).isEqualTo(lastUpdate);
        assertThat(result.checkedAt()).isNotNull();

        LinksRecord insertedLink = dslContext.selectFrom(LINKS)
            .where(LINKS.LINK_ID.eq(result.linkId()))
            .fetchOne();
        assertThat(insertedLink).isNotNull();
        assertThat(insertedLink.getLink()).isEqualTo(link);
    }

    @Test
    void addLink_handleNotExistChat() {
        // Arrange
        Long tgChatId = 999L; // Invalid chat ID
        String link = "http://example.com";
        OffsetDateTime lastUpdate = OffsetDateTime.now();

        // Act & Assert
        NotFoundScrapperException exception = assertThrows(
            NotFoundScrapperException.class,
            () -> jooqLinksRepository.addLink(tgChatId, link, lastUpdate)
        );
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Пользователь не зарегестрирован");
    }

    @Test
    void removeLinkTest() {
        // Arrange
        Long tgChatId = 123L;
        String link = "http://example.com";
        OffsetDateTime lastUpdate = OffsetDateTime.now();
        dslContext.insertInto(CHATS)
            .set(CHATS.TELEGRAM_CHAT_ID, tgChatId)
            .set(CHATS.CREATED_AT, OffsetDateTime.now())
            .execute();

        LinkModel addedLink = jooqLinksRepository.addLink(tgChatId, link, lastUpdate);

        // Act
        LinkModel result = jooqLinksRepository.removeLink(tgChatId, link);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.linkId()).isEqualTo(addedLink.linkId());
        assertThat(result.link()).isEqualTo(link);
        assertThat(result.lastUpdate()).isNull();
        assertThat(result.checkedAt()).isNull();

        // Check if link is removed from database
        LinksRecord removedLink = dslContext.selectFrom(LINKS)
            .where(LINKS.LINK_ID.eq(addedLink.linkId()))
            .fetchOne();
        assertThat(removedLink).isNull();
    }

    @Test
    void removeLink_NotFoundTest() {
        // Arrange
        Long tgChatId = 123L;
        String link = "http://example.com"; // Link not added before

        // Act
        LinkModel result = jooqLinksRepository.removeLink(tgChatId, link);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void existsLinkForChatTest() {
        // Arrange
        Long tgChatId = 123L;
        UUID uuid = UUID.randomUUID();
        String url = "http://example.com";
        dslContext.insertInto(CHATS)
            .set(CHATS.TELEGRAM_CHAT_ID, tgChatId)
            .set(CHATS.CREATED_AT, OffsetDateTime.now())
            .execute();

        dslContext.insertInto(LINKS)
            .set(LINKS.LINK_ID, uuid)
            .set(LINKS.LINK, "http://example.com")
            .set(LINKS.LAST_CHECK, OffsetDateTime.now())
            .set(LINKS.LAST_UPDATE, OffsetDateTime.now())
            .execute();

        dslContext.insertInto(CHAT_LINK_RELATION)
            .set(CHAT_LINK_RELATION.CHAT_ID, tgChatId)
            .set(CHAT_LINK_RELATION.LINK_ID, uuid)
            .execute();

        // Act
        boolean result = jooqLinksRepository.existsLinkForChat(tgChatId, url);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void findStaleLinksTest() {
        // Arrange
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime lastCheck = now.minus(Duration.ofDays(7)); // Stale link
        UUID linkId = UUID.randomUUID();
        dslContext.insertInto(LINKS)
            .set(LINKS.LINK_ID, linkId)
            .set(LINKS.LINK, "http://example.com")
            .set(LINKS.LAST_CHECK, lastCheck)
            .set(LINKS.LAST_UPDATE, lastCheck)
            .execute();

        // Act
        List<LinkModel> result = jooqLinksRepository.findStaleLinks(Duration.ofDays(5)); // Threshold is 5 days

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().linkId()).isEqualTo(linkId);
    }

    @Test
    void updateLastUpdateTest() {
        // Arrange
        UUID linkId = UUID.randomUUID();
        OffsetDateTime lastUpdate = OffsetDateTime.now();
        dslContext.insertInto(LINKS)
            .set(LINKS.LINK_ID, linkId)
            .set(LINKS.LINK, "http://example.com")
            .set(LINKS.LAST_CHECK, lastUpdate)
            .set(LINKS.LAST_UPDATE, lastUpdate)
            .execute();

        // Act
        var newUpdate = lastUpdate.plusDays(1);
        jooqLinksRepository.updateLastUpdate(linkId, newUpdate);

        // Assert
        LinksRecord updatedLink = dslContext.selectFrom(LINKS)
            .where(LINKS.LINK_ID.eq(linkId))
            .fetchOne();
        assertThat(updatedLink).isNotNull();
        assertThat(updatedLink.getLastUpdate().getDayOfMonth()).isEqualTo(newUpdate.getDayOfMonth());
    }

}

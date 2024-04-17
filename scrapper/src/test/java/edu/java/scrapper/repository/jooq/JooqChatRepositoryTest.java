package edu.java.scrapper.repository.jooq;

import edu.java.domain.repository.jooq.JooqChatRepository;
import edu.java.scrapper.IntegrationTest;
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

@SpringBootTest
@Transactional
class JooqChatRepositoryTest extends IntegrationTest {

    @Autowired
    private JooqChatRepository jooqChatRepository;

    @Autowired
    private DSLContext dslContext;

    @Test
    void addChatTest() {
        // Arrange
        Long tgChatId = 123L;

        // Act
        jooqChatRepository.addChat(tgChatId);

        // Assert
        assertThat(jooqChatRepository.findAllChats().size()).isOne();
    }

    @Test
    void removeChatTest() {
        // Arrange
        Long tgChatId = 123L;
        dslContext.insertInto(CHATS)
            .set(CHATS.TELEGRAM_CHAT_ID, tgChatId)
            .set(CHATS.CREATED_AT, OffsetDateTime.now())
            .execute();

        // Act
        jooqChatRepository.removeChat(tgChatId);

        // Assert
        assertThat(jooqChatRepository.findAllChats().size()).isZero();
    }

    @Test
    void findAllChat() {
        // Arrange
        Long tgChatId = 123L;
        dslContext.insertInto(CHATS)
            .set(CHATS.TELEGRAM_CHAT_ID, tgChatId)
            .set(CHATS.CREATED_AT, OffsetDateTime.now())
            .execute();

        // Act
        var chats = jooqChatRepository.findAllChats();

        // Assert
        assertThat(chats.size()).isOne();
    }

    @Test
    void existsChatTest() {
        // Arrange
        Long tgChatId = 123L;
        dslContext.insertInto(CHATS)
            .set(CHATS.TELEGRAM_CHAT_ID, tgChatId)
            .set(CHATS.CREATED_AT, OffsetDateTime.now())
            .execute();

        // Act
        var isExist = jooqChatRepository.existsChat(tgChatId);

        // Assert
        assertThat(isExist).isTrue();
    }

    @Test
    void findChatsByLinkIdTest() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Long chatId = 123L;

        // Вставляем запись в таблицу `chats`
        dslContext.insertInto(CHATS)
            .set(CHATS.TELEGRAM_CHAT_ID, chatId)
            .set(CHATS.CREATED_AT, OffsetDateTime.now())
            .execute();

        // Вставляем запись в таблицу "links"
        dslContext.insertInto(LINKS)
            .set(LINKS.LINK_ID, uuid)
            .set(LINKS.LINK, "https://example.com")
            .set(LINKS.LAST_CHECK, OffsetDateTime.now())
            .set(LINKS.LAST_UPDATE, OffsetDateTime.now())
            .execute();

        // Вставляем связь между UUID и chatId
        dslContext.insertInto(CHAT_LINK_RELATION)
            .set(CHAT_LINK_RELATION.LINK_ID, uuid)
            .set(CHAT_LINK_RELATION.CHAT_ID, chatId)
            .execute();

        // Act
        List<Long> result = jooqChatRepository.findChatsByLinkId(uuid);

        // Assert
        assertThat(result).containsExactly(chatId);
    }

}

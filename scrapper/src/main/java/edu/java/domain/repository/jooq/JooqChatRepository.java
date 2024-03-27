package edu.java.domain.repository.jooq;

import edu.java.domain.repository.ChatRepository;
import edu.java.model.ChatModel;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.domain.jooq.Tables.CHATS;
import static edu.java.domain.jooq.Tables.CHAT_LINK_RELATION;
import static org.jooq.impl.DSL.selectFrom;

@Repository
@RequiredArgsConstructor
public class JooqChatRepository implements ChatRepository {

    private final DSLContext dsl;

    public void addChat(Long chatId) {
        dsl.insertInto(CHATS)
            .set(CHATS.TELEGRAM_CHAT_ID, chatId)
            .set(CHATS.CREATED_AT, OffsetDateTime.now())
            .execute();
    }

    public void removeChat(Long chatId) {
        dsl.deleteFrom(CHAT_LINK_RELATION)
            .where(CHAT_LINK_RELATION.CHAT_ID.eq(chatId))
            .execute();

        dsl.deleteFrom(CHATS)
            .where(CHATS.TELEGRAM_CHAT_ID.eq(chatId))
            .execute();
    }

    public List<ChatModel> findAllChats() {
        return dsl.selectFrom(CHATS)
            .fetchInto(ChatModel.class);
    }
    public boolean existsChat(Long chatId) {
        return dsl.fetchExists(
            selectFrom(CHATS)
                .where(CHATS.TELEGRAM_CHAT_ID.eq(chatId))
        );
    }

    public List<Long> findChatsByLinkId(UUID uuid) {
        return dsl.select(CHAT_LINK_RELATION.CHAT_ID)
            .from(CHAT_LINK_RELATION)
            .where(CHAT_LINK_RELATION.LINK_ID.eq(uuid))
            .fetch(CHAT_LINK_RELATION.CHAT_ID);
    }
}

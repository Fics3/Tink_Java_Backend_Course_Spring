package edu.java.domain.repository;

import edu.java.model.ChatModel;
import java.util.List;
import java.util.UUID;

public interface ChatRepository {

    void addChat(Long chatId);

    void removeChat(Long chatId);

    List<ChatModel> findAllChats();

    boolean existsChat(Long chatId);

    List<Long> findChatsByLinkId(UUID uuid);
}

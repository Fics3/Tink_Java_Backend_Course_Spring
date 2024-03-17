package edu.java.domain.repository;

import java.util.List;
import java.util.UUID;

public interface ChatRepository {

    void addChat(Long chatId);

    void removeChat(Long chatId);

    List<Long> findAllChats();

    boolean existsChat(Long chatId);

    List<Long> findChatsByLinkId(UUID uuid);
}

package edu.java.domain.repository.jpa;

import edu.java.domain.repository.ChatRepository;
import edu.java.domain.repository.jpa.entitesRepository.JpaChatEntityRepository;
import edu.java.domain.repository.jpa.entitesRepository.JpaLinkEntityRepository;
import edu.java.domain.repository.jpa.entity.ChatEntity;
import edu.java.model.ChatModel;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class JpaChatRepository implements ChatRepository {

    private final JpaChatEntityRepository jpaChatEntityRepository;
    private final JpaLinkEntityRepository jpaLinkEntityRepository;

    @Override
    public void addChat(Long chatId) {
        ChatEntity newChat = new ChatEntity(chatId, OffsetDateTime.now());
        jpaChatEntityRepository.save(newChat);
    }

    @Override
    public void removeChat(Long chatId) {
        jpaChatEntityRepository.deleteByTelegramChatId(chatId);
    }

    @Override
    public List<ChatModel> findAllChats() {
        return jpaChatEntityRepository.findAll().stream().map(
            chatEntity -> new ChatModel(chatEntity.getTelegramChatId(), chatEntity.getCreatedAt())
        ).toList();
    }

    @Override
    public boolean existsChat(Long chatId) {
        return jpaChatEntityRepository.existsByTelegramChatId(chatId);
    }

    @Override
    public List<Long> findChatsByLinkId(UUID uuid) {
        return jpaLinkEntityRepository.findByLinkId(uuid).getChats().stream()
            .map(ChatEntity::getTelegramChatId).toList();
    }
}

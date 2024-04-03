package edu.java.domain.repository.jpa.entitiesRepository;

import edu.java.domain.repository.jpa.entity.ChatEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaChatEntityRepository extends JpaRepository<ChatEntity, Long> {

    Optional<ChatEntity> findByTelegramChatId(Long telegramChatId);

    boolean existsByTelegramChatId(Long telegramChatId);

    void deleteByTelegramChatId(Long telegramChatId);
}

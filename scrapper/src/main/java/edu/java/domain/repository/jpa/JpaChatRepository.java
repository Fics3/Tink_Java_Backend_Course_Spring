package edu.java.domain.repository.jpa;

import edu.java.domain.entity.ChatEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaChatRepository extends JpaRepository<ChatEntity, Long> {

    Optional<ChatEntity> findByTelegramChatId(Long telegramChatId);

    boolean existsByTelegramChatId(Long telegramChatId);

    void deleteByTelegramChatId(Long telegramChatId);
}

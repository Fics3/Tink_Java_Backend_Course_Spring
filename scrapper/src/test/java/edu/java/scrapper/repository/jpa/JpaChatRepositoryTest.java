package edu.java.scrapper.repository.jpa;

import edu.java.domain.entity.ChatEntity;
import edu.java.domain.repository.jpa.JpaChatRepository;
import edu.java.scrapper.IntegrationTest;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class JpaChatRepositoryTest extends IntegrationTest {

    @Autowired
    private JpaChatRepository chatRepository;

    @Test
    @Rollback
    @Transactional
    void testFindByTelegramChatId() {
        // Arrange
        Long telegramChatId = 123456L;
        ChatEntity chatEntity = new ChatEntity(telegramChatId, OffsetDateTime.now());
        chatRepository.save(chatEntity);

        // Act
        Optional<ChatEntity> foundChatEntity = chatRepository.findByTelegramChatId(telegramChatId);

        // Assert
        assertTrue(foundChatEntity.isPresent());
        assertEquals(chatEntity.getTelegramChatId(), foundChatEntity.get().getTelegramChatId());
    }

    @Test
    @Rollback
    @Transactional
    void testExistsByTelegramChatId() {
        // Arrange
        Long existingChatId = 123456L;
        Long nonExistingChatId = 78910L;
        ChatEntity chatEntity = new ChatEntity(existingChatId, OffsetDateTime.now());

        chatRepository.save(chatEntity);

        // Act
        boolean existingChatExists = chatRepository.existsByTelegramChatId(existingChatId);
        boolean nonExistingChatExists = chatRepository.existsByTelegramChatId(nonExistingChatId);

        // Assert
        assertTrue(existingChatExists);
        assertFalse(nonExistingChatExists);
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteByTelegramChatId() {
        // Arrange
        Long chatIdToDelete = 123456L;
        ChatEntity chatEntity = new ChatEntity(chatIdToDelete, OffsetDateTime.now());
        chatRepository.save(chatEntity);

        // Act
        chatRepository.deleteByTelegramChatId(chatIdToDelete);
        boolean chatExistsAfterDeletion = chatRepository.existsByTelegramChatId(chatIdToDelete);

        // Assert
        assertFalse(chatExistsAfterDeletion);
    }
}

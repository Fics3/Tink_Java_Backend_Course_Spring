package edu.java.scrapper.service.jpa;

import edu.java.domain.entity.ChatEntity;
import edu.java.domain.repository.jpa.JpaChatRepository;
import edu.java.exception.DuplicateRegistrationScrapperException;
import edu.java.service.jpa.JpaChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaChatServiceTest {

    @Mock
    private JpaChatRepository jpaChatRepository;

    @InjectMocks
    private JpaChatService chatService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddNewChat_Successful() {
        // Arrange
        Long tgChatId = 123456L;
        when(jpaChatRepository.existsByTelegramChatId(tgChatId)).thenReturn(false);

        // ActS
        chatService.add(tgChatId);

        // Assert
        verify(jpaChatRepository, times(1)).save(any(ChatEntity.class));
    }

    @Test
    void testAddExistingChat_DuplicateRegistrationScrapperException() {
        // Arrange
        Long tgChatId = 123456L;
        when(jpaChatRepository.existsByTelegramChatId(tgChatId)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateRegistrationScrapperException.class, () -> chatService.add(tgChatId));

        // Verify that addChat method is not called
        verify(jpaChatRepository, never()).save(any(ChatEntity.class));
    }

    @Test
    void testRemoveChat() {
        // Arrange
        Long tgChatId = 123456L;

        // Act
        chatService.remove(tgChatId);

        // Assert
        verify(jpaChatRepository, times(1)).deleteByTelegramChatId(tgChatId);
    }

}

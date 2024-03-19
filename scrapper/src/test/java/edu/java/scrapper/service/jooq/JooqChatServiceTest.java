package edu.java.scrapper.service.jooq;

import edu.java.domain.repository.jooq.JooqChatRepository;
import edu.java.exception.DuplicateRegistrationScrapperException;
import edu.java.service.jooq.JooqChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JooqChatServiceTest {

    @Mock
    private JooqChatRepository jooqChatRepository;

    @InjectMocks
    private JooqChatService chatService = new JooqChatService();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testAddNewChat_Successful() {
        // Arrange
        Long tgChatId = 123456L;
        when(jooqChatRepository.existsChat(tgChatId)).thenReturn(false);

        // ActS
        chatService.add(tgChatId);

        // Assert
        verify(jooqChatRepository, times(1)).addChat(tgChatId);
    }

    @Test
    void testAddExistingChat_DuplicateRegistrationScrapperException() {
        // Arrange
        Long tgChatId = 123456L;
        when(jooqChatRepository.existsChat(tgChatId)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateRegistrationScrapperException.class, () -> {
            chatService.add(tgChatId);
        });

        // Verify that addChat method is not called
        verify(jooqChatRepository, never()).addChat(anyLong());
    }

    @Test
    void testRemoveChat() {
        // Arrange
        Long tgChatId = 123456L;

        // Act
        chatService.remove(tgChatId);

        // Assert
        verify(jooqChatRepository, times(1)).removeChat(tgChatId);
    }
}

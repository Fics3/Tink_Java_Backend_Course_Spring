package edu.java.scrapper.service.jdbc;

import edu.java.domain.repository.jdbc.JdbcChatRepository;
import edu.java.exception.DuplicateRegistrationScrapperException;
import edu.java.service.jdbc.JdbcChatService;
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

public class JdbcChatServiceTest {

    @Mock
    private JdbcChatRepository jdbcChatRepository;

    @InjectMocks
    private JdbcChatService chatService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddNewChat_Successful() {
        // Arrange
        Long tgChatId = 123456L;
        when(jdbcChatRepository.existsChat(tgChatId)).thenReturn(false);

        // ActS
        chatService.add(tgChatId);

        // Assert
        verify(jdbcChatRepository, times(1)).addChat(tgChatId);
    }

    @Test
    void testAddExistingChat_DuplicateRegistrationScrapperException() {
        // Arrange
        Long tgChatId = 123456L;
        when(jdbcChatRepository.existsChat(tgChatId)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateRegistrationScrapperException.class, () -> chatService.add(tgChatId));

        // Verify that addChat method is not called
        verify(jdbcChatRepository, never()).addChat(anyLong());
    }

    @Test
    void testRemoveChat() {
        // Arrange
        Long tgChatId = 123456L;

        // Act
        chatService.remove(tgChatId);

        // Assert
        verify(jdbcChatRepository, times(1)).removeChat(tgChatId);
    }
}

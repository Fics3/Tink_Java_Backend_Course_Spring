package edu.java.bot.service.commands;

import edu.java.bot.model.User;
import edu.java.bot.service.NotificationService;
import edu.java.bot.service.commands.resourcesHandlers.ChainResourceHandler;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrackCommandServiceTest {

    @Mock
    private ChainResourceHandler chainResourceHandler;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TrackCommandService trackCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("registered user - should use method for checking link")
    void testExecuteSuccessfully() {
        // Arrange
        long chatId = 123456L;
        String message = "/track https://github.com/example";

        // Mock LinkMap
        User user = new User(chatId);
        Map<Long, User> linkMap = Map.of(chatId, user);

        when(notificationService.getLinkMap()).thenReturn(linkMap);

        when(chainResourceHandler
            .handleLink(
                chatId,
                "https://github.com/example",
                notificationService
            )).thenReturn("Ссылка добавлена");

        // Act
        String result = trackCommand.execute(chatId, message, notificationService);

        // Assert
        assertThat(result).isEqualTo("Ссылка добавлена");
    }

    @Test
    @DisplayName("not registered user - should not add link")
    void testExecuteNotRegistered() {
        // Arrange
        long chatId = 789012L;
        String message = "/track https://github.com/example";

        when(notificationService.getLinkMap()).thenReturn(new HashMap<>());

        when(chainResourceHandler.handleLink(anyLong(), anyString(), any(NotificationService.class)))
            .thenReturn("Link added successfully");

        // Act
        String result = trackCommand.execute(chatId, message, notificationService);

        // Assert
        assertThat(result).isEqualTo("Для отслеживания ссылок необходимо зарегестрироваться /start");
        verify(chainResourceHandler, never()).handleLink(anyLong(), anyString(), any(NotificationService.class));
    }

    @Test
    @DisplayName("handle invalid link format")
    void testExecuteInvalidFormat() {
        // Arrange
        long chatId = 123456L;
        String message = "/track";

        Map<Long, User> linkMap = new HashMap<>();
        User user = new User(chatId);
        linkMap.put(chatId, user);
        when(notificationService.getLinkMap()).thenReturn(linkMap);

        // Act
        String result = trackCommand.execute(chatId, message, notificationService);

        // Assert
        assertThat(result).isEqualTo("Неверный формат команды: /track {URL}");
        verify(chainResourceHandler, never()).handleLink(anyLong(), anyString(), any(NotificationService.class));
    }
}

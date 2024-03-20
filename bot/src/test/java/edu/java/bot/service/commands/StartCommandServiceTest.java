package edu.java.bot.service.commands;

import edu.java.bot.service.NotificationService;
import edu.java.bot.service.ScrapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StartCommandServiceTest {

    @Mock
    private ScrapperService scrapperService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private StartCommandService startCommandService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("should return welcome message")
    public void testExecuteReturnWelcomeMessage() {
        // Arrange
        long chatId = 123456789;
        String message = "/start";
        doNothing().when(scrapperService).registerChat(chatId);
        // Act
        String result = startCommandService.execute(chatId, message, notificationService);

        // Assert
        assertThat(result).isEqualTo(
            "Привет, я умею отслеживать вопросы на stackoverflow и репозитории на github");
    }

    @Test
    @DisplayName("verify that chat registered")
    public void testExecuteRegisterNewChat() {
        // Arrange
        long chatId = 123456789;
        String message = "/start";
        doNothing().when(scrapperService).registerChat(chatId);
        // Act
        startCommandService.execute(chatId, message, notificationService);

        // Assert
        verify(scrapperService, times(1)).registerChat(chatId);
    }

}

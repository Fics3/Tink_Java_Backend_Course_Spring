package edu.java.bot.service.commands;

import edu.java.bot.service.NotificationService;
import edu.java.bot.service.ScrapperService;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;

public class UntrackCommandServiceTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private ScrapperService scrapperService;

    @InjectMocks
    private UntrackCommandService untrackCommandService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Valid command and valid URL - returns success message")
    public void testExecute_ValidCommandAndValidUrl_ReturnsSuccessMessage() throws URISyntaxException {
        // Arrange
        long chatId = 123456789;
        String message = "/untrack https://example.com";
        URI uri = new URI("https://example.com");
        doNothing().when(scrapperService).deleteLink(chatId, uri);

        // Act
        String result = untrackCommandService.execute(chatId, message, notificationService);

        // Assert
        assertThat(result).isEqualTo("Для просмотра ваших ссылок введите /list");
    }

    @Test
    @DisplayName("Invalid command format - returns error message")
    public void testExecute_InvalidCommandFormat_ReturnsErrorMessage() {
        // Arrange
        long chatId = 123456789;
        String message = "/untrack";
        String expectedErrorMessage = "Неверный формат команды: /untrack {URL}";

        // Act
        String result = untrackCommandService.execute(chatId, message, notificationService);

        // Assert
        assertThat(result).isEqualTo(expectedErrorMessage);
    }

}

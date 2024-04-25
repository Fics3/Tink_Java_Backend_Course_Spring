package edu.java.bot.service.commands;

import edu.java.bot.service.NotificationService;
import edu.java.bot.service.ScrapperService;
import edu.java.bot.service.commands.resourcesHandlers.ChainResourceHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class TrackCommandServiceTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private ChainResourceHandler chainResourceHandler;

    @Mock
    private ScrapperService scrapperService;

    @InjectMocks
    private TrackCommandService trackCommandService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Valid command and link not tracked - returns success message")
    public void testExecuteReturnsSuccessMessage() {
        // Arrange
        long chatId = 123456789;
        String message = "/track https://example.com";
        String resource = "https://example.com";
        when(chainResourceHandler.handleLink(chatId, resource)).thenReturn("Success");

        // Act
        String result = trackCommandService.execute(chatId, message, notificationService);

        // Assert
        assertThat(result).isEqualTo("Success");
    }

    @Test
    @DisplayName("Invalid command - returns error message")
    public void testExecuteReturnsErrorMessage() {
        // Arrange
        long chatId = 123456789;
        String message = "/track";
        String errorMessage = "Неверный формат команды: /track {URL}";

        // Act
        String result = trackCommandService.execute(chatId, message, notificationService);

        // Assert
        assertThat(result).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Not registered - returns not registered message")
    public void testExecuteReturnsNoRegisteredMessage() {
        // Arrange
        long chatId = 123456789;
        String message = "/track https://example.com";
        when(chainResourceHandler.handleLink(chatId, "https://example.com"))
            .thenThrow(new WebClientResponseException(
                HttpStatus.BAD_REQUEST.value(),
                "No Registered",
                null,
                null,
                null
            ));

        // Act
        String result = trackCommandService.execute(chatId, message, notificationService);

        // Assert
        assertThat(result).isEqualTo("Для отслеживания ссылок необходимо зарегестрироваться /start");
    }

    @Test
    @DisplayName("Link already tracked - returns link already tracked message")
    public void testExecuteReturnsLinkAlreadyExistMessage() {
        // Arrange
        long chatId = 123456789;
        String message = "/track https://example.com";
        when(chainResourceHandler.handleLink(chatId, "https://example.com"))
            .thenThrow(new WebClientResponseException(
                HttpStatus.CONFLICT.value(),
                "Link Already Exists",
                null,
                null,
                null
            ));

        // Act
        String result = trackCommandService.execute(chatId, message, notificationService);

        // Assert
        assertThat(result).isEqualTo("Вы уже отслеживаете такую ссылку");
    }

    @Test
    @DisplayName("Internal server error - returns default error message")
    public void testExecuteReturnsDefaultErrorMessage() {
        // Arrange
        long chatId = 123456789;
        String message = "/track https://example.com";
        when(chainResourceHandler.handleLink(chatId, "https://example.com"))
            .thenThrow(new WebClientResponseException(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                null,
                null,
                null
            ));

        // Act
        String result = trackCommandService.execute(chatId, message, notificationService);

        // Assert
        assertThat(result).isEqualTo("Ошибка, попробуйте позже");
    }
}

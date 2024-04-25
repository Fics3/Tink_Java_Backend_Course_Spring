package edu.java.bot.service.commands;

import edu.java.bot.service.NotificationService;
import edu.java.bot.service.ScrapperService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.example.dto.LinkResponse;
import org.example.dto.ListLinkResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ListCommandServiceTest {

    @Mock
    private ScrapperService scrapperService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ListCommandService listCommandService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("should return links if response not empty")
    public void testExecuteReturnsSavedLinks() {
        // Arrange
        long chatId = 123456789;
        String message = "/list";
        List<LinkResponse> linkResponses =
            List.of(new LinkResponse(URI.create("https://example.com"), OffsetDateTime.now()));
        ListLinkResponse listLinkResponse = new ListLinkResponse(linkResponses, 1);
        when(scrapperService.getAllLinks(chatId)).thenReturn(listLinkResponse);

        // Act
        String result = listCommandService.execute(chatId, message, notificationService);

        // Assert
        assertThat(result).isEqualTo("Сохраненные ссылки:\nhttps://example.com");
        verify(scrapperService, times(1)).getAllLinks(chatId);
    }

    @Test
    @DisplayName("should return empty links message if response empty")
    public void testExecuteReturnsNoSavedLinksMessage() {
        // Arrange
        long chatId = 123456789;
        String message = "/list";
        List<LinkResponse> linkResponses =
            List.of();
        ListLinkResponse listLinkResponse = new ListLinkResponse(linkResponses, 1);
        when(scrapperService.getAllLinks(chatId)).thenReturn(listLinkResponse);

        // Act
        String result = listCommandService.execute(chatId, message, notificationService);

        // Assert
        assertThat(result).isEqualTo("Нет сохраненных ссылок");
        verify(scrapperService, times(1)).getAllLinks(chatId);
    }

    @Test
    @DisplayName("should return not registered message if user not registered")
    public void testExecuteReturnsNoRegisteredMessage() {
        // Arrange
        long chatId = 123456789;
        String message = "/list";
        when(scrapperService.getAllLinks(chatId)).thenThrow(WebClientResponseException.class);

        // Act
        String result = listCommandService.execute(chatId, message, notificationService);

        // Assert
        assertThat(result).isEqualTo("Для просмотра ссылок необходимо зарегестрироваться /start");
        verify(scrapperService, times(1)).getAllLinks(chatId);
    }
}

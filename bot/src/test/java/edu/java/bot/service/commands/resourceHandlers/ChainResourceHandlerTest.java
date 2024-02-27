package edu.java.bot.service.commands.resourceHandlers;

import edu.java.bot.model.User;
import edu.java.bot.service.NotificationService;
import edu.java.bot.service.commands.resourcesHandlers.ChainResourceHandler;
import edu.java.bot.service.commands.resourcesHandlers.GitHubHandler;
import edu.java.bot.service.commands.resourcesHandlers.Link;
import edu.java.bot.service.commands.resourcesHandlers.StackOverflowHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChainResourceHandlerTest {
    @ParameterizedTest
    @DisplayName("valid link case - should add link to database")
    @ValueSource(strings = {"https://github.com", "https://stackoverflow.com"})
    void testHandleLinkSuccessfully() {
        // Arrange
        long chatId = 123456L;
        String message = "https://github.com";
        NotificationService notificationService = mock(NotificationService.class);
        ChainResourceHandler handler = spy(new GitHubHandler());

        // Mocking LinkMap
        Map<Long, User> linkMap = new HashMap<>();
        User user = mock(User.class); // Mocking User object
        List<Link> links = spy(new ArrayList<>()); // Spy on the list of links
        when(user.getLinks()).thenReturn(links);
        linkMap.put(chatId, user);
        when(notificationService.getLinkMap()).thenReturn(linkMap);

        // Act
        String result = handler.handleLink(chatId, message, notificationService);

        // Assert
        assertThat(result).isEqualTo("Ссылка добавлена");
        verify(user.getLinks(), times(1)).add(any(Link.class));
    }

    @Test
    @DisplayName("invalid link case - should not add link to data base")
    void testHandleLinkFailed() {
        // Arrange
        long chatId = 123456L;
        String message = "https://example.com";
        NotificationService notificationService = mock(NotificationService.class);
        ChainResourceHandler handler = spy(new GitHubHandler());

        // Mocking LinkMap
        Map<Long, User> linkMap = new HashMap<>();
        User user = mock(User.class);
        List<Link> links = spy(new ArrayList<>());
        when(user.getLinks()).thenReturn(links);
        linkMap.put(chatId, user);
        when(notificationService.getLinkMap()).thenReturn(linkMap);

        // Act
        String result = handler.handleLink(chatId, message, notificationService);

        // Assert
        verify(links, never()).add(any(Link.class));
    }

    @Test
    @DisplayName("should pass link to the next handler")
    void testLinkWith_ShouldPassLinkToNextHandler() {
        // Arrange
        long chatId = 123456L;
        String message = "https://stackoverflow.com";
        NotificationService notificationService = mock(NotificationService.class);
        ChainResourceHandler handler = spy(new GitHubHandler());
        handler.linkWith(new StackOverflowHandler());

        // Mocking LinkMap
        Map<Long, User> linkMap = new HashMap<>();
        User user = mock(User.class); // Mocking User object
        List<Link> links = spy(new ArrayList<>()); // Spy on the list of links
        when(user.getLinks()).thenReturn(links);
        linkMap.put(chatId, user);
        when(notificationService.getLinkMap()).thenReturn(linkMap);

        // Act
        String result = handler.handleLink(chatId, message, notificationService);

        // Assert
        assertThat(result).isEqualTo("Ссылка добавлена");
        verify(user.getLinks(), times(1)).add(any(Link.class)); // Verifying add method on the list of links
    }
}

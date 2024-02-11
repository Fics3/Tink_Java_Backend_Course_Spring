package edu.java.bot.commands;

import edu.java.bot.model.User;
import edu.java.bot.model.commands.UntrackCommand;
import edu.java.bot.model.commands.resourcesHandlers.Link;
import edu.java.bot.service.NotificationService;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UntrackCommandTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private UntrackCommand untrackCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("registered user - should remove link from tracked list")
    void testExecuteSuccessfully() throws URISyntaxException {
        // Arrange
        long chatId = 123456L;
        String urlToRemove = "https://example.com";
        NotificationService notificationService = mock(NotificationService.class);

        // Создание мока User с одной ссылкой
        User mockedUser = mock(User.class);
        ArrayList<Link> links = new ArrayList<>();
        links.add(Link.parse(urlToRemove));
        when(mockedUser.getLinks()).thenReturn(links);

        // Устанавливаем мокированное значение для getLinkMap()
        when(notificationService.getLinkMap()).thenReturn(Map.of(chatId, mockedUser));

        UntrackCommand untrackCommand = new UntrackCommand();

        // Act
        String result = untrackCommand.execute(chatId, "/untrack " + urlToRemove, notificationService);

        // Assert
        assertThat(result).isEqualTo("Для просмотра ваших ссылок введите /list");
        assertThat(links.size()).isZero();
    }

    @Test
    @DisplayName("unregistered user - should return message to register first")
    void testExecuteUnregisteredUser() {
        // Arrange
        long chatId = 123456L;
        String message = "/untrack https://github.com/example";

        // Mock LinkMap with null user
        Map<Long, User> linkMap = new HashMap<>();
        when(notificationService.getLinkMap()).thenReturn(linkMap);

        // Act
        String result = untrackCommand.execute(chatId, message, notificationService);

        // Assert
        assertThat(result).isEqualTo("Для удаления ссылок необходимо зарегестрироваться /start");
    }
}


package edu.java.bot.service.commands;

import edu.java.bot.model.User;
import edu.java.bot.service.NotificationService;
import edu.java.bot.service.commands.resourcesHandlers.Link;
import java.net.URISyntaxException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ListCommandServiceTest {

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        Map<String, CommandService> commandMap = mock();
        notificationService = new NotificationService(commandMap);
    }

    @Test
    @DisplayName("not registered - should return special message")
    void testExecuteNotRegistered() {
        // Arrange
        ListCommandService listCommand = new ListCommandService();
        long chatId = 123;

        // Act
        String result = listCommand.execute(chatId, "some message", notificationService);

        // Assert
        assertThat(result).isEqualTo("Для просмотра ссылок необходимо зарегестрироваться /start");
    }

    @Test
    @DisplayName("registered user - no links - special message")
    public void testExecute_UserRegisteredWithEmptyLinkList_ReturnsNoLinksMessage() {
        // Arrange
        ListCommandService listCommand = new ListCommandService();
        long chatId = 1234567L;
        notificationService.getLinkMap().put(chatId, new User(chatId));

        // Act
        String result = listCommand.execute(chatId, "/list", notificationService);

        // Assert
        assertThat(result).isEqualTo("Нет сохраненных ссылок");
    }

    @Test
    @DisplayName("several links - check correct message format")
    public void testExecute_UserRegisteredWithLinks_ReturnsFormattedLinks() throws URISyntaxException {
        // Arrange
        ListCommandService listCommand = new ListCommandService();
        long chatId = 1234567L;
        User user = new User(chatId);
        user.addLink(Link.parse("https://example.com"));
        user.addLink(Link.parse("https://example.org"));
        notificationService.getLinkMap().put(chatId, user);

        // Act
        String result = listCommand.execute(chatId, "/list", notificationService);

        // Assert
        assertThat(result).isEqualTo("Сохраненные ссылки:\nhttps://example.com\nhttps://example.org");
    }
}

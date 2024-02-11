package edu.java.bot.commands;

import edu.java.bot.model.User;
import edu.java.bot.model.commands.CommandManager;
import edu.java.bot.model.commands.ListCommand;
import edu.java.bot.model.commands.resourcesHandlers.Link;
import edu.java.bot.service.NotificationService;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ListCommandTest {

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        CommandManager commandManager = mock(CommandManager.class);
        notificationService = new NotificationService(commandManager);
    }

    @Test
    @DisplayName("not registered - should return special message")
    void testExecuteNotRegistered() {
        // Arrange
        ListCommand listCommand = new ListCommand();
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
        ListCommand listCommand = new ListCommand();
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
        ListCommand listCommand = new ListCommand();
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

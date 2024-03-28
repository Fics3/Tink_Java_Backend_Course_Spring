package edu.java.bot.service.commands;

import edu.java.bot.model.User;
import edu.java.bot.service.NotificationService;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StartCommandServiceTest {

    @Test
    @DisplayName("register user - data base should be not null")
    void testExecute() {
        // Arrange
        long chatId = 123456L;
        NotificationService notificationService = mock(NotificationService.class);

        // Ensure that getLinkMap() returns a non-null value
        when(notificationService.getLinkMap()).thenReturn(new HashMap<>());

        StartCommandService startCommand = new StartCommandService();

        // Act
        String result = startCommand.execute(chatId, "", notificationService);

        // Assert
        assertThat(result).isEqualTo("Hello world");

        Map<Long, User> linkMap = notificationService.getLinkMap();
        assertThat(linkMap).isNotEmpty();

    }

    @Test
    @DisplayName("should return command name")
    void testGetName() {
        // Arrange
        StartCommandService startCommand = new StartCommandService();

        // Act
        String result = startCommand.getName();

        // Assert
        assertThat(result).isEqualTo("/start");
    }

    @Test
    @DisplayName("should return command description")
    void testGetDescription() {
        // Arrange
        StartCommandService startCommand = new StartCommandService();

        // Act
        String result = startCommand.getDescription();

        // Assert
        assertThat(result).isEqualTo("registration");
    }
}

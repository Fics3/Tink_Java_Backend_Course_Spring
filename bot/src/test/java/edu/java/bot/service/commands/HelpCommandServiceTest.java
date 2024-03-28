package edu.java.bot.service.commands;

import edu.java.bot.service.NotificationService;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HelpCommandServiceTest {
    @Test
    @DisplayName("should return all commands")
    void testExecute() {
        // Arrange
        long chatId = 123456L;
        NotificationService notificationService = mock(NotificationService.class);

        // Mocking the command map
        Map<String, CommandService> commandMap = new HashMap<>();
        commandMap.put("/command1", mock(CommandService.class));
        commandMap.put("/command2", mock(CommandService.class));

        when(notificationService.getCommandMap()).thenReturn(commandMap);

        HelpCommandService helpCommand = new HelpCommandService();

        // Act
        String result = helpCommand.execute(chatId, "", notificationService);

        // Assert
        assertEquals("[/command2, /command1]", result);
    }

    @Test
    @DisplayName("should return command name")
    void testGetName() {
        // Arrange
        HelpCommandService helpCommand = new HelpCommandService();

        // Act
        String result = helpCommand.getName();

        // Assert
        assertThat(result).isEqualTo("/help");
    }

    @Test
    @DisplayName("should return command description")
    void testGetDescription() {
        // Arrange
        HelpCommandService helpCommand = new HelpCommandService();

        // Act
        String result = helpCommand.getDescription();

        // Assert
        assertThat(result).isEqualTo("show all bot commands");
    }
}

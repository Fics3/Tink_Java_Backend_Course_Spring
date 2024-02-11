package edu.java.bot.service;

import edu.java.bot.model.commands.Command;
import edu.java.bot.model.commands.CommandManager;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationServiceTest {

    private NotificationService notificationService;
    private CommandManager commandManagerMock;

    @BeforeEach
    public void setup() {
        commandManagerMock = mock(CommandManager.class);
        notificationService = new NotificationService(commandManagerMock);
    }

    @Test
    @DisplayName("getCommand - valid command no special message")
    public void testGetCommand_ValidCommand_ReturnsExpectedResult() {
        // Arrange
        long chatId = 123;
        String message = "/list";
        Command listCommandMock = mock(Command.class);
        when(listCommandMock.getName()).thenReturn("/list");
        when(listCommandMock.execute(chatId, message, notificationService)).thenReturn(
            "List command executed successfully");
        when(commandManagerMock.getCommandMap()).thenReturn(Map.of("/list", listCommandMock));

        // Act
        String result = notificationService.getCommand(chatId, message);

        // Assert
        assertThat(result).isEqualTo("List command executed successfully");
    }

    @Test
    @DisplayName("getCommand - invalid command special message")
    public void testGetCommand_InvalidCommand_ReturnsErrorMessage() {
        // Arrange
        long chatId = 123;
        String message = "/invalid";
        when(commandManagerMock.getCommandMap()).thenReturn(new HashMap<>());

        // Act
        String result = notificationService.getCommand(chatId, message);

        // Assert
        assertThat(result).isEqualTo("Такой команды не существует, введите /help, чтобы увидеть список доступных команд");
    }

    @Test
    @DisplayName("getCommand - null map special message")
    public void testGetCommand_NullCommandMap_ReturnsErrorMessage() {
        // Arrange
        long chatId = 123;
        String message = "/list";
        when(commandManagerMock.getCommandMap()).thenReturn(null);

        // Act
        String result = notificationService.getCommand(chatId, message);

        // Assert
        assertThat(result).isEqualTo("Такой команды не существует, введите /help, чтобы увидеть список доступных команд");
    }

    @Test
    @DisplayName("getCommand - null command special message")
    public void testGetCommand_NullCommand_ReturnsErrorMessage() {
        // Arrange
        long chatId = 123;
        String message = "/list";
        Map<String, Command> commandMap = new HashMap<>();
        commandMap.put("/list", null);
        when(commandManagerMock.getCommandMap()).thenReturn(commandMap);

        // Act
        String result = notificationService.getCommand(chatId, message);

        // Assert
        assertThat(result).isEqualTo("Такой команды не существует, введите /help, чтобы увидеть список доступных команд");
    }
}

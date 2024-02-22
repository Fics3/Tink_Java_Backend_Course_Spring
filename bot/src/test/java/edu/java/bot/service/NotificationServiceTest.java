package edu.java.bot.service;

import edu.java.bot.controller.TestConfig;
import edu.java.bot.service.commands.CommandService;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {TestConfig.class})
public class NotificationServiceTest {

    @Qualifier("commandMap")
    @Mock
    private Map<String, CommandService> commandMapMock;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getCommand - valid command no special message")
    public void testGetCommand_ValidCommand_ReturnsExpectedResult() {
        // Arrange
        long chatId = 123;
        String message = "/list";
        CommandService listCommandMockService = mock(CommandService.class);
        when(commandMapMock.get("/list")).thenReturn(listCommandMockService);
        when(listCommandMockService.getName()).thenReturn("/list");
        when(listCommandMockService.execute(chatId, message, notificationService)).thenReturn(
            "List command executed successfully");

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
        commandMapMock.clear();

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
        commandMapMock = null;

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
        commandMapMock.put("/list", null);

        // Act
        String result = notificationService.getCommand(chatId, message);

        // Assert
        assertThat(result).isEqualTo("Такой команды не существует, введите /help, чтобы увидеть список доступных команд");
    }
}

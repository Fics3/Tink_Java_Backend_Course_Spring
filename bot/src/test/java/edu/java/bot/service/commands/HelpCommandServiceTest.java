package edu.java.bot.service.commands;

import edu.java.bot.service.NotificationService;
import edu.java.bot.service.ScrapperService;
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

public class HelpCommandServiceTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private HelpCommandService helpCommandService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("execute - returns commands")
    public void testExecuteReturnsCommands() {
        // Arrange
        ScrapperService scrapperService = mock(ScrapperService.class);
        Map<String, CommandService> commandMap = new HashMap<>();
        commandMap.put("/start", new StartCommandService(scrapperService));
        commandMap.put("/list", new ListCommandService(scrapperService));
        commandMap.put("/help", new HelpCommandService());
        when(notificationService.getCommandMap()).thenReturn(commandMap);

        // Act
        String result = helpCommandService.execute(123456789, "/help", notificationService);

        // Assert
        assertThat(result).isEqualTo(
            """
                /list - отобразить все отслеживаемые ссылки
                /help - отобразить все команды бота
                /start - регистрация"""
        );
    }

    @Test
    @DisplayName("execute - returns empty string")
    public void testExecuteReturnsEmptyString() {
        // Arrange
        Map<String, CommandService> commandMap = new HashMap<>();
        when(notificationService.getCommandMap()).thenReturn(commandMap);

        // Act
        String result = helpCommandService.execute(123456789, "/help", notificationService);

        // Assert
        assertThat(result).isEmpty();
    }
}

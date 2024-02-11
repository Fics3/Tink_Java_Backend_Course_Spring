package edu.java.bot.controller;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.service.NotificationService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@SpringBootTest(classes = {NotificationUpdateListener.class, TestConfig.class})
class NotificationUpdateListenerTest {

    @Mock
    private TelegramBot telegramBot;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationUpdateListener updateListener;

    @BeforeEach
    void setUp() {
        initMocks(this);
        updateListener = new NotificationUpdateListener(telegramBot, notificationService);
    }

    @Test
    @DisplayName("null update message - no interaction")
    void testProcess_UpdateMessageNull_ShouldNotSendMessage() {
        // Arrange
        Update update = mock(Update.class);
        when(update.message()).thenReturn(null);

        // Act
        int result = updateListener.process(List.of(update));

        // Assert
        verifyNoInteractions(notificationService);
        verifyNoInteractions(telegramBot);
    }

    @Test
    @DisplayName("valid message - interaction with bot")
    public void testProcess_UpdateMessageValid_BotShouldExecute() {
        // Arrange
        Message message = mock(Message.class);
        when(message.text()).thenReturn("/testCommand");

        Update update = mock(Update.class);
        when(update.message()).thenReturn(message);

        Chat chat = mock(Chat.class);
        when(chat.id()).thenReturn(123L);
        when(update.message().chat()).thenReturn(chat);

        when(notificationService.getCommand(anyLong(), eq("/testCommand"))).thenReturn("Test response");

        // Act
        updateListener.process(Collections.singletonList(update));

        //Assert
        verify(notificationService).getCommand(eq(123L), eq("/testCommand"));

        verify(telegramBot).execute(any());
    }
}

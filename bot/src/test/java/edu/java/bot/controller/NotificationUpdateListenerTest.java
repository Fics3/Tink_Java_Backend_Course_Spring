package edu.java.bot.controller;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.service.NotificationService;
import io.micrometer.core.instrument.Counter;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {NotificationUpdateListener.class, TestConfig.class})
class NotificationUpdateListenerTest {

    @MockBean
    private TelegramBot telegramBot;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private Counter messageCounter;

    @Autowired
    private NotificationUpdateListener updateListener;

    @Test
    @DisplayName("null update message - no interaction")
    void testProcess_UpdateMessageNull_ShouldNotSendMessage() {
        // Arrange
        Update update = mock(Update.class);
        when(update.message()).thenReturn(null);

        // Act
        int result = updateListener.process(List.of(update));

        // Assert
        verify(telegramBot).setUpdatesListener(any());
        verifyNoMoreInteractions(telegramBot);
        verify(messageCounter).increment();
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
        verify(notificationService).processCommand(update, telegramBot);
        verify(messageCounter).increment();
    }
}

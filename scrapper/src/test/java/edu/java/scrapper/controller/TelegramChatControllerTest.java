package edu.java.scrapper.controller;

import edu.java.controller.TelegramChatController;
import edu.java.exception.DuplicateRegistrationScrapperException;
import edu.java.exception.InternalServerScrapperException;
import edu.java.exception.NotFoundScrapperException;
import edu.java.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TelegramChatController.class)
public class TelegramChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @Test
    void testRegisterChat() throws Exception {
        // Act&Assert
        mockMvc.perform(post("/tg-chat/{id}", 123)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("Чат зарегестрирован"));
    }

    @Test
    void testDeleteChat() throws Exception {
        // Act&Assert
        mockMvc.perform(delete("/tg-chat/{id}", 123)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("Чат успешно удален"));
    }

    @Test
    void testDuplicateRegistrationException() throws Exception {

        doThrow(new DuplicateRegistrationScrapperException("123", "232")).when(chatService).register();

        mockMvc.perform(post("/tg-chat/{id}", 2323)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
    }

    @Test
    void testHandleInternalServerException() throws Exception {

        doThrow(new InternalServerScrapperException("123", "232")).when(chatService).register();

        mockMvc.perform(post("/tg-chat/{id}", 2323)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testHandleNotFoundException() throws Exception {

        doThrow(new NotFoundScrapperException("123", "232")).when(chatService).delete();

        mockMvc.perform(delete("/tg-chat/{id}", 999)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

}

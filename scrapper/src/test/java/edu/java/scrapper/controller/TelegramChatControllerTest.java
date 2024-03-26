package edu.java.scrapper.controller;

import edu.java.controller.TelegramChatController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(TelegramChatController.class)
public class TelegramChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return status 200 when register chat")
    public void testRegisterChat() throws Exception {
        int chatId = 123;

        mockMvc.perform(MockMvcRequestBuilders.post("/tg-chat/{id}", chatId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("Чат зарегестрирован"));
    }

    @Test
    @DisplayName("Should return status 200 when delete chat")
    public void testDeleteChat() throws Exception {
        int chatId = 456;

        mockMvc.perform(MockMvcRequestBuilders.delete("/tg-chat/{id}", chatId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("Чат успешно удален"));
    }
}

package edu.java.scrapper.controller;

import edu.java.exception.BadRequestScrapperException;
import edu.java.exception.DuplicateRegistrationScrapperException;
import edu.java.exception.InternalServerScrapperException;
import edu.java.rateLimit.RateLimitService;
import edu.java.service.ChatService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TelegramChatController.class)
public class TelegramChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @MockBean
    private RateLimitService rateLimitService;

    @BeforeEach
    public void setUp() {
        Bandwidth bandwidth = Bandwidth.classic(10, Refill.greedy(10, Duration.ofHours(1)));
        when(rateLimitService.resolveBucket(any())).thenReturn(Bucket.builder().addLimit(bandwidth).build());
    }

    @Test
    public void testRegisterChat() throws Exception {
        // Arrange
        Long chatId = 123456L;

        // Act
        MvcResult result = mockMvc.perform(post("/tg-chat/{id}", chatId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        // Assert
        verify(chatService).add(chatId);
    }

    @Test
    public void testDeleteChat() throws Exception {
        // Arrange
        Long chatId = 123456L;

        // Act
        MvcResult result = mockMvc.perform(delete("/tg-chat/{id}", chatId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        // Assert
        verify(chatService).remove(chatId);
    }

    @Test
    public void testRegisterChat_InternalServerError() throws Exception {
        // Arrange
        Long chatId = 123456L;
        doThrow(new InternalServerScrapperException("Internal Server Error", "Description")).when(chatService)
            .add(anyLong());

        // Act & Assert
        mockMvc.perform(post("/tg-chat/{id}", chatId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());
    }

    @Test
    public void testRegisterChat_BadRequest() throws Exception {
        Long chatId = 123456L;
        doThrow(new BadRequestScrapperException("Bad Request", "Description")).when(chatService).add(anyLong());

        mockMvc.perform(post("/tg-chat/{id}", chatId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterChat_Conflict() throws Exception {
        Long chatId = 123456L;
        doThrow(new DuplicateRegistrationScrapperException("Conflict", "Description")).when(chatService)
            .add(anyLong());

        mockMvc.perform(post("/tg-chat/{id}", chatId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
    }
}

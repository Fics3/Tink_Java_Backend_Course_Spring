package edu.java.bot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.exception.BadRequestBotException;
import edu.java.bot.exception.InternalServerBotException;
import edu.java.bot.exception.NotFoundBotException;
import edu.java.bot.service.UpdateService;
import io.micrometer.core.instrument.Counter;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.example.dto.LinkUpdateRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebServerController.class)
public class WebServerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UpdateService updateService;

    @MockBean
    private Counter messageCounter;

    @BeforeAll
    public static void setUpBeforeAll() {
        System.setProperty("SERVER_PORT", "8090");
    }

    @Test
    public void testProcessUpdate_Success() throws Exception {
        // Arrange
        LinkUpdateRequest linkUpdateRequest =
            new LinkUpdateRequest(UUID.randomUUID(), URI.create("dsd"), "SDSD", List.of(1223L));

        // Act&Assert
        mockMvc.perform(post("/updates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(linkUpdateRequest)))
            .andExpect(status().isOk());

        verify(messageCounter).increment();
    }

    @Test
    public void testHandleException_InternalServerBotException() throws Exception {
        // Arrange
        doThrow(new InternalServerBotException("test", "testD"))
            .when(updateService).processUpdate(any(), any());

        LinkUpdateRequest request = new LinkUpdateRequest(UUID.randomUUID(), URI.create("sds"), "sdsd", List.of(1L));

        // Act&Assert
        mockMvc.perform(post("/updates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isInternalServerError());
        verify(messageCounter).increment();
    }

    @Test
    public void testHandleException_BadRequestException() throws Exception {
        doThrow(new BadRequestBotException("test", "testD"))
            .when(updateService).processUpdate(any(), any());

        mockMvc.perform(post("/updates"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testHandleException_NotFoundException() throws Exception {
        // Arrange
        doThrow(new NotFoundBotException("test", "testD"))
            .when(updateService).processUpdate(any(), any());

        LinkUpdateRequest request = new LinkUpdateRequest(UUID.randomUUID(), URI.create("sds"), "sdsd", List.of(1L));

        // Act&Assert
        mockMvc.perform(post("/updates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isNotFound());
        verify(messageCounter).increment();
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

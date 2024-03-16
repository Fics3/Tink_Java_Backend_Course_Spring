package edu.java.bot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.exception.BadRequestBotException;
import edu.java.bot.exception.InternalServerBotException;
import edu.java.bot.exception.NotFoundBotException;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.example.dto.LinkUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebServerController.class)
public class WebServerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WebServerController webServerController;

    @Test
    void testProcessUpdate() throws Exception {
        // Arrange
        LinkUpdateRequest request =
            new LinkUpdateRequest(UUID.randomUUID(), URI.create("example"), "232", List.of(123L));

        // Act&Assert
        MvcResult result = mockMvc.perform(post("/updates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    void testHandleInternalServerException() throws Exception {
        // Arrange
        doThrow(new InternalServerBotException("Internal Server Error", "SDSD"))
            .when(webServerController).processUpdate(any(LinkUpdateRequest.class));

        LinkUpdateRequest request =
            new LinkUpdateRequest(UUID.randomUUID(), URI.create("example"), "232", List.of(123L));

        // Act&Assert
        MvcResult result = mockMvc.perform(post("/updates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isInternalServerError())
            .andReturn();
    }

    @Test
    void testHandleBadRequestException() throws Exception {
        // Arrange
        doThrow(new BadRequestBotException("Bad Request", "SDSD"))
            .when(webServerController).processUpdate(any(LinkUpdateRequest.class));

        LinkUpdateRequest request =
            new LinkUpdateRequest(UUID.randomUUID(), URI.create("example"), "232", List.of(123L));

        // Act&Assert
        MvcResult result = mockMvc.perform(post("/updates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    void testHandleNotFoundBotException() throws Exception {
        // Arrange
        doThrow(new NotFoundBotException("Not Found", "SDSD"))
            .when(webServerController).processUpdate(any(LinkUpdateRequest.class));

        LinkUpdateRequest request =
            new LinkUpdateRequest(UUID.randomUUID(), URI.create("example"), "232", List.of(123L));

        // Act&Assert
        MvcResult result = mockMvc.perform(post("/updates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isNotFound())
            .andReturn();
    }

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package edu.java.scrapper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.controller.LinksController;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import org.example.dto.AddLinkRequest;
import org.example.dto.LinkResponse;
import org.example.dto.ListLinkResponse;
import org.example.dto.RemoveLinkRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LinksController.class)
class LinksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return empty list when getting links")
    void getLinks_shouldReturnEmptyList() throws Exception {
        // Arrange

        // Act & Assert
        mockMvc.perform(get("/links")
                .header("Tg-Chat-Id", 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.links").isArray())
            .andExpect(jsonPath("$.links").isEmpty())
            .andExpect(jsonPath("$.size").value(0));
    }

    @Test
    @DisplayName("Should return link response when adding a link")
    void addLink_shouldReturnLinkResponse() throws Exception {
        // Arrange
        AddLinkRequest addLinkRequest = new AddLinkRequest(URI.create("https://example.com"));

        var expectedTime = OffsetDateTime.now();

        LinkResponse linkResponse = new LinkResponse(URI.create("https://example.com"), OffsetDateTime.now());


        // Act & Assert
        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addLinkRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").value("https://example.com"))
            .andExpect(jsonPath("$.last_update").value(expectedTime));
    }

    @Test
    @DisplayName("Should return link response when delete link")
    void deleteLink_shouldReturnLinkResponse() throws Exception {
        // Arrange
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(URI.create("https://example.com"));

        var expectedTime = OffsetDateTime.now();

        LinkResponse linkResponse = new LinkResponse(URI.create("https://example.com"), OffsetDateTime.now());

        // Act & Assert
        mockMvc.perform(delete("/links")
                .header("Tg-Chat-Id", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(removeLinkRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").value("https://example.com"))
            .andExpect(jsonPath("$.last_update").value(expectedTime));
    }
}

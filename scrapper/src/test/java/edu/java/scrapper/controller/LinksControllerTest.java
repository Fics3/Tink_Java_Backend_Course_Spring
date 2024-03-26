package edu.java.scrapper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.client.ScrapperClient;
import edu.java.controller.LinksController;
import java.net.URI;
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

    @MockBean
    private ScrapperClient scrapperClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return empty list when getting links")
    void getLinks_shouldReturnEmptyList() throws Exception {
        // Arrange
        doReturn(Mono.just(new ListLinkResponse())).when(scrapperClient).getAllLinks(anyLong());

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
        AddLinkRequest addLinkRequest = new AddLinkRequest();
        addLinkRequest.setUri(URI.create("https://example.com"));

        LinkResponse linkResponse = new LinkResponse();
        linkResponse.setId(1);
        linkResponse.setUrl(URI.create("https://example.com"));

        doReturn(Mono.just(linkResponse)).when(scrapperClient).addLink(anyLong(), any(AddLinkRequest.class));

        // Act & Assert
        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addLinkRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.url").value("https://example.com"));
    }

    @Test
    @DisplayName("Should return link response when delete link")
    void deleteLink_shouldReturnLinkResponse() throws Exception {
        // Arrange
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest();
        removeLinkRequest.setLink(URI.create("https://example.com"));

        LinkResponse linkResponse = new LinkResponse();
        linkResponse.setId(1);
        linkResponse.setUrl(URI.create("https://example.com"));

        doReturn(Mono.just(linkResponse)).when(scrapperClient).removeLink(anyLong(), any(RemoveLinkRequest.class));

        // Act & Assert
        mockMvc.perform(delete("/links")
                .header("Tg-Chat-Id", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(removeLinkRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.url").value("https://example.com"));
    }
}

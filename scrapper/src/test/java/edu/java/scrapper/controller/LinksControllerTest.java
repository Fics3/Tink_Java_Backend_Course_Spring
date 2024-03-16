package edu.java.scrapper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.controller.LinksController;
import edu.java.exception.BadRequestScrapperException;
import edu.java.exception.DuplicateLinkScrapperException;
import edu.java.exception.InternalServerScrapperException;
import edu.java.exception.NotFoundScrapperException;
import edu.java.service.LinkService;
import java.net.URI;
import org.example.dto.AddLinkRequest;
import org.example.dto.RemoveLinkRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LinksController.class)
public class LinksControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LinkService linkService;

    @Test
    void testGetLinks() throws Exception {
        Long tgChatId = 123456L;

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Tg-Chat-Id", tgChatId.toString());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", tgChatId.toString()))
            .andExpect(status().isOk())
            .andReturn();

    }

    @Test
    void testAddLink() throws Exception {
        long tgChatId = 123456L;
        String uri = "https://example.com";
        AddLinkRequest addLinkRequest = new AddLinkRequest(URI.create(uri));

        mockMvc.perform(post("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", Long.toString(tgChatId))
                .content(asJsonString(addLinkRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").value(uri));
    }

    @Test
    public void testDeleteLink() throws Exception {
        // Arrange
        Long tgChatId = 12345L;
        String link = "https://example.com";
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(URI.create(link));

        // Act & Assert
        mockMvc.perform(delete("/links").header("Tg-Chat-Id", tgChatId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(removeLinkRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").value(link));
    }

    @Test
    void testHandleDuplicateLinkException() throws Exception {
        // Arrange
        long tgChatId = 123456L;
        String uri = "https://example.com";
        AddLinkRequest addLinkRequest = new AddLinkRequest(URI.create(uri));

        doThrow(DuplicateLinkScrapperException.class).when(linkService).addLink(anyLong(), any(AddLinkRequest.class));

        // Act&Assert
        mockMvc.perform(post("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", Long.toString(tgChatId))
                .content(asJsonString(addLinkRequest)))
            .andExpect(status().isConflict());
    }

    @Test
    void testHandleNotFoundException() throws Exception {
        // Arrange
        long tgChatId = 123456L;
        String link = "https://example.com";
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(URI.create(link));

        doThrow(NotFoundScrapperException.class).when(linkService).removeLink(anyLong(), any(RemoveLinkRequest.class));

        // Act&Assert
        mockMvc.perform(delete("/links").header("Tg-Chat-Id", tgChatId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(removeLinkRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testHandleBadRequestException() throws Exception {
        long tgChatId = 123456L;
        String uri = "https://example.com";
        AddLinkRequest addLinkRequest = new AddLinkRequest(URI.create(uri));

        doThrow(BadRequestScrapperException.class).when(linkService).addLink(anyLong(), any(AddLinkRequest.class));

        mockMvc.perform(post("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", Long.toString(tgChatId))
                .content(asJsonString(addLinkRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testHandleInternalServerException() throws Exception {
        long tgChatId = 123456L;
        String link = "https://example.com";
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(URI.create(link));

        doThrow(InternalServerScrapperException.class).when(linkService)
            .removeLink(anyLong(), any(RemoveLinkRequest.class));

        mockMvc.perform(delete("/links").header("Tg-Chat-Id", tgChatId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(removeLinkRequest)))
            .andExpect(status().isInternalServerError());
    }

    public String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

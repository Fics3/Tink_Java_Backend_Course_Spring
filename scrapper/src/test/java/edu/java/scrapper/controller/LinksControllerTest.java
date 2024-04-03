package edu.java.scrapper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.controller.LinksController;
import edu.java.exception.DuplicateLinkScrapperException;
import edu.java.exception.NotFoundScrapperException;
import edu.java.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import org.example.dto.AddLinkRequest;
import org.example.dto.LinkResponse;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
        when(linkService.findAll(tgChatId)).thenReturn(Collections.emptyList());

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

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Tg-Chat-Id", Long.toString(tgChatId));

        when(linkService.remove(
            anyLong(),
            any(URI.class)
        )).thenAnswer(invocation -> new LinkResponse(URI.create(uri), OffsetDateTime.now()));
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", Long.toString(tgChatId))
                .content(asJsonString(addLinkRequest)))
            .andExpect(status().isOk())
            .andReturn();
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
        verify(linkService).remove(tgChatId, URI.create(link));
    }

    @Test
    public void testDeleteLink_NotFound() throws Exception {
        Long chatId = 123456L;
        doThrow(new NotFoundScrapperException("Conflict", "Description")).when(linkService)
            .remove(anyLong(), any(URI.class));

        mockMvc.perform(delete("/links/{id}", chatId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteLink_Conflict() throws Exception {
        // Arrange
        long tgChatId = 123456L;
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(URI.create("https://example.com"));

        // Stubbing the service method to throw DuplicateLinkScrapperException
        doThrow(new DuplicateLinkScrapperException("Duplicate Link", "Description"))
            .when(linkService).remove(anyLong(), any(URI.class));

        // Act & Assert
        mockMvc.perform(delete("/links")
                .header("Tg-Chat-Id", Long.toString(tgChatId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(removeLinkRequest)))
            .andExpect(status().isConflict());
    }

    public String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

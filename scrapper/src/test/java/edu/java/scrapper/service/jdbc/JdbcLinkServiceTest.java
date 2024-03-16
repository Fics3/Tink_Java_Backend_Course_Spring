package edu.java.scrapper.service.jdbc;

import edu.java.exception.DuplicateLinkScrapperException;
import edu.java.model.LinkModel;
import edu.java.repository.LinksRepository;
import edu.java.service.LinkService;
import edu.java.service.jdbc.JdbcLinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.example.dto.LinkResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JdbcLinkServiceTest {

    @Mock
    private LinksRepository linksRepository;

    @InjectMocks
    private LinkService linkService = new JdbcLinkService();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this); // Инициализация моков перед каждым тестом
    }

    @Test
    void testAddLink_Successful() {
        // Arrange
        Long tgChatId = 1234L;
        UUID linkId = UUID.randomUUID();
        URI url = URI.create("https://example.com");
        when(linksRepository.existsLinkForChat(tgChatId, url.toString())).thenReturn(false);
        LinkModel expectedLink = new LinkModel(linkId, url.toString(), OffsetDateTime.now(), OffsetDateTime.now());
        when(linksRepository.addLink(tgChatId, url.toString())).thenReturn(expectedLink);

        // Act
        LinkModel result = linkService.add(tgChatId, url);

        // Assert
        assertEquals(expectedLink, result);
    }

    @Test
    void testAddLink_DuplicateLinkScrapperException() {
        // Arrange
        Long tgChatId = 123456L;
        URI url = URI.create("https://example.com");
        when(linksRepository.existsLinkForChat(tgChatId, url.toString())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateLinkScrapperException.class, () -> {
            linkService.add(tgChatId, url);
        });

        // Verify that addLink method is not called
        verify(linksRepository, never()).addLink(anyLong(), anyString());
    }

    @Test
    void testRemoveLink_Successful() {
        // Arrange
        Long tgChatId = 1234L;
        UUID linkId = UUID.randomUUID();
        URI url = URI.create("https://example.com");
        when(linksRepository.existsLinkForChat(tgChatId, url.toString())).thenReturn(false);
        LinkModel expectedLink = new LinkModel(linkId, url.toString(), OffsetDateTime.now(), OffsetDateTime.now());
        when(linksRepository.removeLink(tgChatId, url.toString())).thenReturn(expectedLink);

        // Act
        LinkModel result = linkService.remove(tgChatId, url);

        // Assert
        assertEquals(expectedLink, result);
    }

    @Test
    void testFindAllLinks_Successful() {
        // Arrange
        Long tgChatId = 123456L;
        UUID uuid = UUID.randomUUID();
        List<LinkModel> links = new ArrayList<>();
        links.add(new LinkModel(uuid, "https://example1.com", OffsetDateTime.now(), OffsetDateTime.now()));
        links.add(new LinkModel(uuid, "https://example2.com", OffsetDateTime.now(), OffsetDateTime.now()));
        when(linksRepository.findAllLinks()).thenReturn(links);

        // Act
        List<LinkResponse> result = linkService.findAll(tgChatId);

        // Assert
        assertEquals(links.size(), result.size());
    }
}

package edu.java.scrapper.service;

import edu.java.domain.repository.ChatRepository;
import edu.java.domain.repository.GithubRepositoryRepository;
import edu.java.domain.repository.LinksRepository;
import edu.java.domain.repository.StackoverflowQuestionRepository;
import edu.java.exception.DuplicateLinkScrapperException;
import edu.java.model.LinkModel;
import edu.java.service.LinkService;
import edu.java.service.linkAdder.LinkAdder;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.example.dto.LinkResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LinkServiceTest {

    @Mock
    private ChatRepository chatRepository;
    @Mock
    private LinksRepository linksRepository;
    @Mock
    private LinkAdder linkAdder;
    @Mock
    private Map<String, LinkAdder> linkAdders;
    @Mock
    private GithubRepositoryRepository githubRepositoryRepository;
    @Mock
    private StackoverflowQuestionRepository stackoverflowQuestionRepository;
    @InjectMocks
    private LinkService linkService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        linkAdders = new HashMap<>();
        linkAdders.put("test.com", linkAdder);
        linkService =
            new LinkService(linkAdders, linksRepository, githubRepositoryRepository, stackoverflowQuestionRepository);
    }

    @Test
    void testAddLink_Successful() {
        // Arrange
        Long tgChatId = 1234L;
        chatRepository.addChat(tgChatId);

        UUID linkId = UUID.randomUUID();
        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        URI url = URI.create("https://test.com");
        when(linksRepository.existsLinkForChat(tgChatId, url.toString())).thenReturn(false);

        LinkModel expectedLink = new LinkModel(linkId, url.toString(), offsetDateTime, offsetDateTime);
        when(linkAdder.addLink(eq(url), eq(tgChatId))).thenReturn(
            expectedLink);
        // Act
        LinkModel result = linkService.add(tgChatId, url);

        // Assert
        assertThat(result).isEqualTo(expectedLink);
    }

    @Test
    void testAddLink_DuplicateLinkScrapperException() {
        // Arrange
        Long tgChatId = 123456L;
        URI url = URI.create("https://github.com");
        when(linksRepository.existsLinkForChat(tgChatId, url.toString())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateLinkScrapperException.class, () -> linkService.add(tgChatId, url));

        // Verify that addLink method is not called
        verify(linksRepository, never()).addLink(anyLong(), anyString(), any());
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
        assertThat(result).isEqualTo(expectedLink);
    }

    @Test
    void testFindAllLinks_Successful() {
        // Arrange
        Long tgChatId = 123456L;
        UUID uuid = UUID.randomUUID();
        List<LinkModel> links = new ArrayList<>();
        links.add(new LinkModel(uuid, "https://example1.com", OffsetDateTime.now(), OffsetDateTime.now()));
        links.add(new LinkModel(uuid, "https://example2.com", OffsetDateTime.now(), OffsetDateTime.now()));
        when(linksRepository.findLinksByChatId(tgChatId)).thenReturn(links);

        // Act
        List<LinkResponse> result = linkService.findAll(tgChatId);

        // Assert
        assertThat(result.size()).isEqualTo(links.size());
    }
}

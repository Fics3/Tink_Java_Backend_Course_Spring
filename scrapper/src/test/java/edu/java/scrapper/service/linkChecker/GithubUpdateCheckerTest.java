package edu.java.scrapper.service.linkChecker;

import edu.java.client.BotClient;
import edu.java.client.GithubClient;
import edu.java.domain.repository.ChatRepository;
import edu.java.domain.repository.GithubRepositoryRepository;
import edu.java.domain.repository.LinksRepository;
import edu.java.model.LinkModel;
import edu.java.service.updateChecker.GithubUpdateChecker;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.example.dto.GithubRepositoryResponse;
import org.example.dto.LinkUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class GithubUpdateCheckerTest {
    @Mock
    private GithubClient githubClient;

    @Mock
    private BotClient botClient;

    @Mock
    private GithubRepositoryRepository jooqGithubRepositoryRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private LinksRepository jooqLinksRepository;

    @InjectMocks
    private GithubUpdateChecker updateChecker;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    public void testProcessUrlUpdates() {
        // Arrange
        LinkModel linkModel = new LinkModel(
            UUID.randomUUID(),
            "https://github/repo/12345678",
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        when(githubClient.fetchRepository(any(URI.class))).thenReturn(Mono.just(new GithubRepositoryResponse(
            null,
            null,
            null,
            OffsetDateTime.now(),
            null
        )));

        when(botClient.sendUpdate(any(LinkUpdateRequest.class))).thenReturn(Mono.empty());
        when(jooqGithubRepositoryRepository.getRepositoryByLinkId(any(UUID.class))).thenReturn(null);
        when(chatRepository.findChatsByLinkId(any(UUID.class))).thenReturn(null);
        // Act
        int updateCount = updateChecker.processUrlUpdates(linkModel, 0);

        // Assert
        verify(botClient, times(1)).sendUpdate(any(LinkUpdateRequest.class));
        verify(jooqLinksRepository, times(1)).updateLastUpdate(any(UUID.class), any(OffsetDateTime.class));
    }
}

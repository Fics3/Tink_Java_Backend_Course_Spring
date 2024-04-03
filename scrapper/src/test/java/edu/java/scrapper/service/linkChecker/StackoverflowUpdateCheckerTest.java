package edu.java.scrapper.service.linkChecker;

import edu.java.client.BotClient;
import edu.java.client.StackoverflowClient;
import edu.java.domain.repository.ChatRepository;
import edu.java.domain.repository.LinksRepository;
import edu.java.domain.repository.StackoverflowQuestionRepository;
import edu.java.model.LinkModel;
import edu.java.service.updateChecker.StackoverflowUpdateChecker;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.example.dto.LinkUpdateRequest;
import org.example.dto.StackoverflowQuestionResponse;
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

public class StackoverflowUpdateCheckerTest {

    @Mock
    private StackoverflowClient stackoverflowClient;

    @Mock
    private BotClient botClient;

    @Mock
    private LinksRepository jooqLinksRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private StackoverflowQuestionRepository stackoverflowQuestionRepository;

    @InjectMocks
    private StackoverflowUpdateChecker updateChecker;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    public void testProcessUrlUpdates() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        LinkModel linkModel = new LinkModel(
            uuid,
            "https://stackoverflow.com/questions/12345678",
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        when(stackoverflowClient.fetchQuestion(any(URI.class))).thenReturn(Mono.just(new StackoverflowQuestionResponse(
            List.of(new StackoverflowQuestionResponse.ItemResponse(null, null, null, OffsetDateTime.now(), null)))));

        when(botClient.sendUpdate(any(LinkUpdateRequest.class))).thenReturn(Mono.empty());
        when(stackoverflowQuestionRepository.getQuestionByLinkId(uuid)).thenReturn(null);
        // Act
        int updateCount = updateChecker.processUrlUpdates(linkModel, 0);

        // Assert
        verify(botClient, times(1)).sendUpdate(any(LinkUpdateRequest.class));
        verify(jooqLinksRepository, times(1)).updateLastUpdate(any(UUID.class), any(OffsetDateTime.class));
    }

}

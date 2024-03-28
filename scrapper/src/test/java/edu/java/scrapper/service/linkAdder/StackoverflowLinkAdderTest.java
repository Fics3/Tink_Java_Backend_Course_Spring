package edu.java.scrapper.service.linkAdder;

import edu.java.client.StackoverflowClient;
import edu.java.domain.repository.LinksRepository;
import edu.java.domain.repository.StackoverflowQuestionRepository;
import edu.java.model.LinkModel;
import edu.java.service.linkAdder.StackoverflowLinkAdder;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.example.dto.StackoverflowQuestionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StackoverflowLinkAdderTest {

    @Mock
    private StackoverflowClient stackoverflowClient;

    @Mock
    private LinksRepository linksRepository;

    @Mock
    private StackoverflowQuestionRepository stackoverflowQuestionRepository;

    @InjectMocks
    private StackoverflowLinkAdder linkAdder;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddLink() {
        // Arrange
        URI url = URI.create("https://stackoverflow.com/question/123");
        Long tgChatId = 123456L;
        OffsetDateTime updatedAt = OffsetDateTime.now();
        int answersCount = 1000;
        LinkModel linkModel = new LinkModel(
            null,
            null,
            updatedAt,
            updatedAt
        );

        when(stackoverflowClient.fetchQuestion(any(URI.class))).thenReturn(Mono.just(new StackoverflowQuestionResponse(
            List.of(new StackoverflowQuestionResponse.ItemResponse(
                null,
                null,
                null,
                updatedAt,
                answersCount
            )))
        ));

        when(linksRepository.addLink(
            any(Long.class),
            any(String.class),
            any(OffsetDateTime.class)
        )).thenReturn(linkModel);
        when(stackoverflowQuestionRepository.addQuestion(linkModel, answersCount)).thenReturn(linkModel);

        // Act
        linkAdder.addLink(url, tgChatId);

        // Assert
        verify(linksRepository, times(1)).addLink(
            eq(tgChatId),
            eq(url.toString()),
            eq(updatedAt)
        );

        verify(stackoverflowQuestionRepository, times(1))
            .addQuestion(eq(linkModel), eq(answersCount));
    }
}

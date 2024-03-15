package edu.java.scrapper.client;

import edu.java.client.BotClient;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.example.dto.LinkUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BotClientTest {

    @Mock
    private WebClient botWebClient;

    private BotClient botClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        botClient = new BotClient(botWebClient);
    }

    @Test
    @DisplayName("Should do post")
    void sendUpdate_shouldReturnEmptyMono() {
        // Given
        LinkUpdateRequest updateRequest =
            new LinkUpdateRequest(
                UUID.randomUUID(),
                URI.create("http://example.com"),
                "12",
                List.of(1L)
            );

        // Mock WebClient behavior
        when(botWebClient.post())
            .thenReturn(WebClient.builder().baseUrl("http://example.com").build().post());

        // When
        Mono<Void> result = botClient.sendUpdate(updateRequest);

        verify(botWebClient, times(1)).post();
    }
}

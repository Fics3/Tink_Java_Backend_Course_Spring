package edu.java.scrapper.client;

import edu.java.client.BotClient;
import org.example.dto.LinkUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(BotClient.class)
class BotClientTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private BotClient botWebClient;

    @Test
    @DisplayName("Should return 404 when post")
    void registerChat_shouldReturnInternalServerError() {
        // Arrange
        long chatId = 123;
        Mockito.when(botWebClient.sendUpdate(any(LinkUpdateRequest.class)))
            .thenReturn(Mono.error(new RuntimeException("Some error message")));

        // Act & Assert
        webTestClient.post()
            .uri("/updates", chatId)
            .exchange()
            .expectStatus().is4xxClientError();
    }
}

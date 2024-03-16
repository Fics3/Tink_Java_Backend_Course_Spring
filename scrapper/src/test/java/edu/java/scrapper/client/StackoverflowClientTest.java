package edu.java.scrapper.client;

import edu.java.client.StackoverflowClient;
import org.example.dto.StackoverflowQuestionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebFluxTest(StackoverflowClient.class)
public class StackoverflowClientTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private StackoverflowClient stackoverflowClient;

    @Test
    public void testFetchQuestion() {
        // Arrange
        StackoverflowQuestionResponse response = mock(StackoverflowQuestionResponse.class);

        when(stackoverflowClient.fetchQuestion(anyLong()))
            .thenReturn(Mono.just(response));

        // Act&Assert
        webTestClient.get()
            .uri("/questions/123456")
            .exchange()
            .expectStatus().isNotFound()
            .expectBody();
    }
}

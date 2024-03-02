package edu.java.scrapper.client;

import edu.java.client.ScrapperClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(ScrapperClient.class)
class ScrapperClientTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ScrapperClient scrapperClient;

    @Test
    @DisplayName("Should return code 500 when register chat")
    void registerChat_shouldReturnInternalServerError() {
        // Arrange
        long chatId = 123;
        Mockito.when(scrapperClient.registerChat(chatId))
            .thenReturn(Mono.error(new RuntimeException("Some error message")));

        // Act & Assert
        webTestClient.post()
            .uri("/tg-chat/{id}", chatId)
            .exchange()
            .expectStatus().is5xxServerError() // Change to 5xx status expectation
            .expectBody()
            .jsonPath("$.description").isEqualTo("Внутренняя ошибка сервера");
    }

    @Test
    @DisplayName("Should return code 500 when delete chat")
    void deleteChat_shouldReturnInternalServerError() {
        // Arrange
        long chatId = 123;
        Mockito.when(scrapperClient.deleteChat(chatId))
            .thenReturn(Mono.error(new RuntimeException("Some error message")));

        // Act & Assert
        webTestClient.delete()
            .uri("/tg-chat/{id}", chatId)
            .exchange()
            .expectStatus().is5xxServerError() // Change to 5xx status expectation
            .expectBody()
            .jsonPath("$.description").isEqualTo("Внутренняя ошибка сервера");
    }

}

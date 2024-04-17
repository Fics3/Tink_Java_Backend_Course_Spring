package edu.java.bot;

import edu.java.bot.client.ScrapperClient;
import org.junit.jupiter.api.BeforeAll;
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

    @BeforeAll
    static void setUp() {
        System.setProperty("SERVER_PORT", "8090");
    }

    @Test
    @DisplayName("Should return code 404 when register chat")
    void registerChat_shouldReturnInternalServerError() {
        // Arrange
        long chatId = 123;
        Mockito.when(scrapperClient.registerChat(chatId))
            .thenReturn(Mono.error(new RuntimeException("Some error message")));

        // Act & Assert
        webTestClient.post()
            .uri("/tg-chat/{id}", chatId)
            .exchange()
            .expectStatus().is4xxClientError();
    }

    @Test
    @DisplayName("Should return code 404 when delete chat")
    void deleteChat_shouldReturnInternalServerError() {
        // Arrange
        long chatId = 123;
        Mockito.when(scrapperClient.deleteChat(chatId))
            .thenReturn(Mono.error(new RuntimeException("Some error message")));

        // Act & Assert
        webTestClient.delete()
            .uri("/tg-chat/{id}", chatId)
            .exchange()
            .expectStatus().is4xxClientError();
    }

}

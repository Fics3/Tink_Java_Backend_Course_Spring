package edu.java.bot.client;

import edu.java.bot.configuration.ClientConfig;
import edu.java.bot.configuration.retry.ConstantRetry;
import edu.java.bot.configuration.retry.ExponentRetry;
import edu.java.bot.configuration.retry.LinearRetry;
import edu.java.bot.configuration.retry.RetryPolicy;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.assertThat;

@WebFluxTest(ScrapperClient.class)
class ScrapperClientTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ScrapperClient scrapperWebClient;

    private ScrapperClient scrapperClient;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8080);
        String baseUrl = mockWebServer.url("/").toString();
        ClientConfig clientConfig = new ClientConfig(
            new ClientConfig.ScrapperProperties(
                "/tg-chat/{id}",
                "/links",
                "Tg-Chat-Id",
                baseUrl,
                new RetryPolicy(3, Duration.ofSeconds(2),
                    RetryPolicy.BackoffStrategy.linear, List.of(500, 404)
                )
            ));
        scrapperClient = new ScrapperClient(WebClient.builder().baseUrl("http://localhost:8080").build(), clientConfig,
            Map.of(RetryPolicy.BackoffStrategy.exponent, new ExponentRetry(),
                RetryPolicy.BackoffStrategy.linear, new LinearRetry(),
                RetryPolicy.BackoffStrategy.constant, new ConstantRetry()
            )
        );
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("Should return code 404 when register chat")
    void registerChat_shouldReturnInternalServerError() {
        // Arrange
        long chatId = 123;
        Mockito.when(scrapperWebClient.registerChat(chatId))
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
        Mockito.when(scrapperWebClient.deleteChat(chatId))
            .thenReturn(Mono.error(new RuntimeException("Some error message")));

        // Act & Assert
        webTestClient.delete()
            .uri("/tg-chat/{id}", chatId)
            .exchange()
            .expectStatus().is4xxClientError();
    }

    @Test
    void sendUpdate_Retry_Success() {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.NOT_FOUND.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.OK.value())
            .setBody("{}")
            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        // Act
        scrapperClient.getAllLinks(123L).block();

        // Assert
        assertThat(mockWebServer.getRequestCount()).isEqualTo(3);
    }

    @Test
    void sendUpdate_Retry_Failure() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.NOT_FOUND.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        // Act & Assert
        Assertions.assertThrows(
            Exception.class,
            () -> scrapperClient.getAllLinks(123L).block(Duration.ofSeconds(5))
        );

        Thread.sleep(3000);

        assertThat(mockWebServer.getRequestCount()).isEqualTo(3);
    }

}

package edu.java.scrapper.client;

import edu.java.client.BotClient;
import edu.java.configuration.ClientConfig;
import edu.java.configuration.retry.ConstantRetry;
import edu.java.configuration.retry.ExponentRetry;
import edu.java.configuration.retry.LinearRetry;
import edu.java.configuration.retry.RetryPolicy;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.example.dto.LinkUpdateRequest;
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
import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(BotClient.class)
class BotClientTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private BotClient botWebClient;

    private BotClient botClient;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8090);
        String baseUrl = mockWebServer.url("/").toString();
        ClientConfig clientConfig = new ClientConfig(
            null,
            null,
            new ClientConfig.BotClient(
                baseUrl,
                new RetryPolicy(3, Duration.ofSeconds(2),
                    RetryPolicy.BackoffStrategy.linear, List.of(500)
                )
            )
        );
        botClient = new BotClient(
            WebClient.builder().baseUrl("http://localhost:8090").build(), clientConfig,
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

    @Test
    void sendUpdate_Retry_Success() {
        // Arrange

        LinkUpdateRequest linkUpdateRequest =
            new LinkUpdateRequest(UUID.randomUUID(), URI.create("223"), "", List.of());

        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.OK.value())
            .setBody("{}")
            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        // Act
        botClient.sendUpdate(linkUpdateRequest).block();

        // Assert
        assertThat(mockWebServer.getRequestCount()).isEqualTo(3);
    }

    @Test
    void sendUpdate_Retry_Failure() throws Exception {
        // Arrange
        LinkUpdateRequest linkUpdateRequest =
            new LinkUpdateRequest(UUID.randomUUID(), URI.create("223"), "", List.of());

        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        // Act & Assert
        Assertions.assertThrows(
            Exception.class,
            () -> botClient.sendUpdate(linkUpdateRequest).block(Duration.ofSeconds(5))
        );

        Thread.sleep(3000);

        assertThat(mockWebServer.getRequestCount()).isEqualTo(3);
    }
}

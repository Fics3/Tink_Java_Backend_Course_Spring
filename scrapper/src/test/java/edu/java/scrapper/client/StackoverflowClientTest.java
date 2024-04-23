package edu.java.scrapper.client;

import edu.java.client.StackoverflowClient;
import edu.java.configuration.ClientConfig;
import edu.java.configuration.retry.ConstantRetry;
import edu.java.configuration.retry.ExponentRetry;
import edu.java.configuration.retry.LinearRetry;
import edu.java.configuration.retry.RetryPolicy;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.example.dto.StackoverflowQuestionResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebFluxTest(StackoverflowClient.class)
public class StackoverflowClientTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private StackoverflowClient stackoverflowWebClient;

    private StackoverflowClient stackoverflowClient;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = mockWebServer.url("/").toString();
        ClientConfig clientConfig = new ClientConfig(
            new ClientConfig.StackoverflowProperties(
                null,
                baseUrl,
                "/questions/%d?site=stackoverflow",
                new RetryPolicy(3, Duration.ofSeconds(2),
                    RetryPolicy.BackoffStrategy.linear, List.of(500)
                )
            ),
            null,
            null
        );
        stackoverflowClient =
            new StackoverflowClient(
                clientConfig,
                clientConfig.stackoverflowWebClient(),
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
    public void testFetchQuestion() {
        // Arrange
        StackoverflowQuestionResponse response = mock(StackoverflowQuestionResponse.class);

        when(stackoverflowWebClient.fetchQuestion(URI.create("https://api.stackexchange.com")))
            .thenReturn(Mono.just(response));

        // Act&Assert
        webTestClient.get()
            .uri("/questions/123456")
            .exchange()
            .expectStatus().isNotFound()
            .expectBody();
    }

    @Test
    void fetchQuestion_Retry_Success() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.OK.value())
            .setBody("{\"title\":\"Test question title\",\"body\":\"Test question body\"}")
            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        URI uri = new URI(mockWebServer.url("/") + "/questions/123");

        // Act
        stackoverflowClient.fetchQuestion(uri).block();

        // Assert
        assertThat(mockWebServer.getRequestCount()).isEqualTo(3);
    }

    @Test
    void fetchQuestion_Retry_Failure() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        URI uri = new URI(mockWebServer.url("/") + "/questions/123");

        // Act & Assert
        Assertions.assertThrows(
            Exception.class,
            () -> stackoverflowClient.fetchQuestion(uri).block(Duration.ofSeconds(5))
        );
        Thread.sleep(3000);

        assertThat(mockWebServer.getRequestCount()).isEqualTo(3);
    }
}

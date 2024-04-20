package edu.java.scrapper.client;

import edu.java.client.StackoverflowClient;
import edu.java.configuration.ClientConfig;
import edu.java.configuration.retry.RetryPolicy;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.example.dto.StackoverflowQuestionResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.assertThat;

public class RetryStackoverflowClientTest {

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
            new StackoverflowClient(clientConfig, clientConfig.stackoverflowWebClient());
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void fetchQuestion_Retry_Success() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
            .setBody("{\"title\":\"Test question title\",\"body\":\"Test question body\"}")
            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        URI uri = new URI(mockWebServer.url("/") + "/questions/123");

        // Act
        Mono<StackoverflowQuestionResponse> responseMono = stackoverflowClient.fetchQuestion(uri);

        // Assert
        StackoverflowQuestionResponse response = responseMono.block(Duration.ofSeconds(5));
        assertThat(response).isNotNull();
    }

    @Test
    void fetchQuestion_Retry_Failure() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        URI uri = new URI(mockWebServer.url("/").toString());

        // Act & Assert
        Assertions.assertThrows(
            Exception.class,
            () -> stackoverflowClient.fetchQuestion(uri).block(Duration.ofSeconds(5))
        );
    }
}

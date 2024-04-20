package edu.java.bot.client;

import edu.java.bot.configuration.ClientConfig;
import edu.java.bot.configuration.retry.RetryPolicy;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.example.dto.LinkUpdateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import static org.assertj.core.api.Assertions.assertThat;

public class RetryScrapperClientTest {

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
                    RetryPolicy.BackoffStrategy.linear, List.of(500)
                )
            ));
        scrapperClient = new ScrapperClient(WebClient.builder().baseUrl("http://localhost:8090").build(), clientConfig);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void sendUpdate_Retry_Success() {
        // Arrange

        LinkUpdateRequest linkUpdateRequest =
            new LinkUpdateRequest(UUID.randomUUID(), URI.create("223"), "", List.of());

        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.OK.value())
            .setBody("{}")
            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        // Act
        var response = scrapperClient.getAllLinks(123L);

        // Assert
        assertThat(response).isNotNull();
    }

}

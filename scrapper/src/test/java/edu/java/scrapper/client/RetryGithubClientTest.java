package edu.java.scrapper.client;

import edu.java.client.GithubClient;
import edu.java.configuration.ClientConfig;
import edu.java.configuration.retry.RetryPolicy;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.example.dto.GithubRepositoryResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import static org.assertj.core.api.Assertions.assertThat;

public class RetryGithubClientTest {

    private GithubClient githubClient;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = mockWebServer.url("/").toString();
        ClientConfig clientConfig = new ClientConfig(null,
            new ClientConfig.GithubProperties(null, baseUrl, "/repos/%s/%s", new RetryPolicy(1, Duration.ofSeconds(2),
                RetryPolicy.BackoffStrategy.linear, List.of(500)
            )), null
        );
        githubClient = new GithubClient(clientConfig, clientConfig.githubWebClient());
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void fetchRepository_Retry_Success() {
        // Arrange
        String baseUrl = mockWebServer.url("/").toString();

        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(HttpStatus.OK.value())
            .setBody("{\"name\":\"Test Repository\",\"description\":\"Test description\"}")
            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        URI uri = URI.create(baseUrl + "/repos/12/12");
        // Act
        GithubRepositoryResponse response = githubClient.fetchRepository(uri).block();

        // Assert
        assertThat(response).isNotNull();
    }

    @Test
    void fetchRepository_Retry_Failure() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        URI uri = new URI(mockWebServer.url("/").toString());

        // Act & Assert
        Assertions.assertThrows(Exception.class, () -> githubClient.fetchRepository(uri).block(Duration.ofSeconds(5)));
    }
}

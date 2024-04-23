package edu.java.scrapper.client;

import edu.java.client.GithubClient;
import edu.java.configuration.ClientConfig;
import edu.java.configuration.retry.ConstantRetry;
import edu.java.configuration.retry.ExponentRetry;
import edu.java.configuration.retry.LinearRetry;
import edu.java.configuration.retry.RetryPolicy;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.example.dto.GithubRepositoryResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(GithubClient.class)
public class GithubClientTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ClientConfig clientConfig;

    @MockBean
    private GithubClient githubWebClient;

    private GithubClient githubClient;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = mockWebServer.url("/").toString();
        ClientConfig clientConfig = new ClientConfig(null,
            new ClientConfig.GithubProperties(null, baseUrl, "/repos/%s/%s",
                new RetryPolicy(3, Duration.ofSeconds(2),
                    RetryPolicy.BackoffStrategy.linear, List.of(500)
                )
            ), null
        );
        githubClient = new GithubClient(clientConfig, clientConfig.githubWebClient(),
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
    @DisplayName("test for check the required response body")
    public void testFetchRepository() {
        // Arrange
        GithubRepositoryResponse response = new GithubRepositoryResponse("TEST", "TEST_FULL", URI.create("sdsd"),
            OffsetDateTime.now(), 123
        );

        when(clientConfig.githubProperties()).thenReturn(new ClientConfig.GithubProperties(
            "github.com",
            "https://api.github.com",
            "/repos/%s/%s",
            new RetryPolicy()
        ));

        when(githubWebClient.fetchRepository(any(URI.class)))
            .thenReturn(Mono.just(response));

        // Act & Assert
        webTestClient.get()
            .uri("/repositories/testOwner/testRepo")
            .exchange()
            .expectStatus().isNotFound()
            .expectBody();
    }

    @Test
    void fetchRepository_Retry_Success() {
        // Arrange
        String baseUrl = mockWebServer.url("/").toString();

        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(HttpStatus.OK.value())
            .setBody("{\"name\":\"Test Repository\",\"description\":\"Test description\"}")
            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        URI uri = URI.create(baseUrl + "/repos/12/12");
        // Act
        githubClient.fetchRepository(uri).block();

        // Assert
        assertThat(mockWebServer.getRequestCount()).isEqualTo(3);
    }

    @Test
    void fetchRepository_Retry_Failure() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        URI uri = new URI(mockWebServer.url("/") + "/repos/12/12");

        // Act & Assert
        Assertions.assertThrows(Exception.class, () -> githubClient.fetchRepository(uri).block(Duration.ofSeconds(5)));

        Thread.sleep(3000);

        assertThat(mockWebServer.getRequestCount()).isEqualTo(3);
    }
}



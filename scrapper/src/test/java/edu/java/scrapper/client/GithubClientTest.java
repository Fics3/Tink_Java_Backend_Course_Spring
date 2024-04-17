package edu.java.scrapper.client;

import edu.java.client.GithubClient;
import edu.java.configuration.ClientConfig;
import java.net.URI;
import java.time.OffsetDateTime;
import org.example.dto.GithubRepositoryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
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
            "/repos/%s/%s"
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

}

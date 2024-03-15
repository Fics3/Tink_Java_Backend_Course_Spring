package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.client.GithubClient;
import edu.java.configuration.ApplicationConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.net.URI;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GithubClientTest {

    @Configuration
    static class TestConfig {
        @Bean
        public WebClient.Builder webClientBuilder() {
            return WebClient.builder();
        }
    }

    @MockBean
    private ApplicationConfig applicationConfig;

    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void setUp() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    public static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("test for check the required response body")
    public void testFetchRepository() {
        // Arrange
        String owner = "testOwner";
        String repo = "testRepo";

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/repos/testOwner/testRepo"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"name\":\"testRepo\",\"full_name\":\"testOwner/testRepo\",\"owner\":\"testOwner\",\"description\":\"Test Repo\",\"html_url\":\"https://github.com/testOwner/testRepo\"}")
            ));

        when(applicationConfig.githubProperties()).thenReturn(new ApplicationConfig.GithubProperties("/repos/%s/%s", "https://api.github.com"));
        // Act
        WebClient githubWebClient = WebClient.builder().baseUrl("http://localhost:" + wireMockServer.port()).build();
        GithubClient gitHubClient = new GithubClient(applicationConfig, githubWebClient);

        // Assert
        StepVerifier.create(gitHubClient.fetchRepository(owner, repo))
            .expectNextMatches(response ->
                response.name().equals("testRepo") &&
                    response.fullName().equals("testOwner/testRepo") &&
                    response.htmlUrl().equals(URI.create("https://github.com/testOwner/testRepo"))
            )
            .expectComplete()
            .verify();
    }
}

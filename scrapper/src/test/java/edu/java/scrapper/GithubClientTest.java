package edu.java.scrapper;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.client.GithubClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

public class GithubClientTest {
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
                .withBody(
                    "{\"name\":\"testRepo\",\"full_name\":\"testOwner/testRepo\",\"owner\":\"testOwner\",\"description\":\"Test Repo\",\"html_url\":\"https://github.com/testOwner/testRepo\"}")
            ));

        // Act
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:" + wireMockServer.port()).build();
        GithubClient gitHubClient = new GithubClient(webClient);

        // Assert
        StepVerifier.create(gitHubClient.fetchRepository(owner, repo))
            .expectNextMatches(response ->
                response.getName().equals("testRepo") &&
                    response.getFullName().equals("testOwner/testRepo") &&
                    response.getOwner().equals("testOwner") &&
                    response.getDescription().equals("Test Repo") &&
                    response.getHtmlUrl().equals("https://github.com/testOwner/testRepo")
            )
            .expectComplete()
            .verify();
    }
}

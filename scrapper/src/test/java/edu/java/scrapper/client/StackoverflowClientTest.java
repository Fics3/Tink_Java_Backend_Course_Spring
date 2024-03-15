package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.client.StackoverflowClient;
import edu.java.configuration.ApplicationConfig;
import java.net.URI;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StackoverflowClientTest {
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
    public void testFetchQuestion() {
        // Arrange
        long questionId = 123456;
        String order = "activity";
        String sort = "desc";
        OffsetDateTime fixedTime = OffsetDateTime.parse("2022-02-21T12:34:56Z");

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/questions/123456"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(
                    "{\"question_id\":123456,\"title\":\"Test Question\",\"link\":\"https://stackoverflow.com/q/123456\",\"last_activity_date\":\"" +
                        fixedTime + "\"}")
            ));

        // Act
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:" + wireMockServer.port()).build();
        ApplicationConfig applicationConfig = mock(ApplicationConfig.class);
        when(applicationConfig.stackoverflowProperties())
            .thenReturn(new ApplicationConfig.StackoverflowProperties(
                "/questions/%d?order=%s&sort=%s&site=stackoverflow"));
        StackoverflowClient stackOverflowClient = new StackoverflowClient(applicationConfig, webClient);

        // Assert
        StepVerifier.create(stackOverflowClient.fetchQuestion(questionId, sort, order))
            // Then
            .expectNextMatches(response ->
                response.questionId() == 123456 &&
                    response.title().equals("Test Question") &&
                    response.link().equals(URI.create("https://stackoverflow.com/q/123456")) &&
                    response.lastActivityDate().equals(fixedTime)
            )
            .expectComplete()
            .verify();
    }

}

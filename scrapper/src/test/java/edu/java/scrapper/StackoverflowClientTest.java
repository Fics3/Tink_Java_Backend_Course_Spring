package edu.java.scrapper;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.stackoverflow.StackoverflowClient;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

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
        StackoverflowClient stackOverflowClient = new StackoverflowClient(webClient);

        // Assert
        StepVerifier.create(stackOverflowClient.fetchQuestion(questionId, sort, order))
            // Then
            .expectNextMatches(response ->
                response.getQuestionId() == 123456 &&
                    response.getTitle().equals("Test Question") &&
                    response.getLink().equals("https://stackoverflow.com/q/123456") &&
                    response.getLastActivityDate().equals(fixedTime)
            )
            .expectComplete()
            .verify();
    }

}

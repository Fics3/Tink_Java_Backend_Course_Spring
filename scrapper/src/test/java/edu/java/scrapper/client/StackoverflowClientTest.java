package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.client.StackoverflowClient;
import edu.java.configuration.ApplicationConfig;
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
        Integer questionId = 123456;
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
                "/questions/%d?site=stackoverflow", "https://stackexchange.com"));
        StackoverflowClient stackOverflowClient = new StackoverflowClient(applicationConfig, webClient);

        // Assert
        StepVerifier.create(stackOverflowClient.fetchQuestion(questionId));
        // Then

    }

}

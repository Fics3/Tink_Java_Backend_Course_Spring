package edu.java.bot;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.bot.client.ScrapperClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.mockito.Mockito.when;

class ScrapperClientTest {

    private static WireMockServer wireMockServer;

    private static WebClient webClient;

    private static ScrapperClient scrapperClient;

    @BeforeAll
    static void setUp() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();

        WireMock.configureFor("localhost", wireMockServer.port());

        webClient = WebClient.builder().baseUrl("http://localhost:" + wireMockServer.port()).build();
        scrapperClient = new ScrapperClient(webClient);
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void registerChat_Success() {
        Long chatId = 123L;

        // Stubbing the method call on the mock object directly
        when(scrapperClient.registerChat(chatId)).thenReturn(Mono.empty()); // Assuming registerChat returns a Mono<Void>

        // Mocking the method call to return the base URL
        when(scrapperClient.getChat()).thenReturn("/tg-chat/");

        // Invoke the method being tested
        scrapperClient.registerChat(chatId).block();

        // Assert whatever is necessary for successful registration
    }

    @Test
    void deleteChat_Success() {
        Long chatId = 123L;

        stubFor(delete(urlEqualTo("/tg-chat/" + chatId))
            .willReturn(aResponse()
                .withStatus(200)));

        scrapperClient.deleteChat(chatId).block();
        // Assert whatever is necessary for successful deletion
    }

    // Similarly, write tests for other methods like getAllLinks, addLink, removeLink
}

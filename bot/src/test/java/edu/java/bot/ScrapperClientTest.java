package edu.java.bot;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import edu.java.bot.client.ScrapperClient;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import lombok.extern.log4j.Log4j2;
import org.example.dto.AddLinkRequest;
import org.example.dto.LinkResponse;
import org.example.dto.ListLinkResponse;
import org.example.dto.RemoveLinkRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Log4j2
class ScrapperClientTest {

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("Register Chat - Successfully Registered")
    void registerChatTest() {
        long chatId = 123456789L;
        ScrapperClient scrapperClient = mock(ScrapperClient.class);
        Mockito.when(scrapperClient.getChat()).thenReturn("/tg-chat/");

        when(scrapperClient.registerChat(anyLong())).thenReturn(Mono.just("Registration successful"));

        stubFor(post(urlEqualTo(scrapperClient.getChat() + chatId))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("Registration successful")));

        StepVerifier.create(scrapperClient.registerChat(chatId))
            .expectNext("Registration successful")
            .expectComplete()
            .verify();
    }

    @Test
    @DisplayName("Delete Chat - Successfully Deleted")
    void deleteChatTest() {
        long chatId = 123456789L;
        ScrapperClient scrapperClient = mock(ScrapperClient.class);
        Mockito.when(scrapperClient.getChat()).thenReturn("/tg-chat/");

        when(scrapperClient.deleteChat(anyLong())).thenReturn(Mono.just("Delete successful"));

        stubFor(delete(urlEqualTo(scrapperClient.getChat() + chatId))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("Delete successful")));

        StepVerifier.create(scrapperClient.deleteChat(chatId))
            .expectNext("Delete successful")
            .expectComplete()
            .verify();
    }

    @Test
    @DisplayName("Get All Links - Successfully Retrieved")
    void getAllLinks_SuccessfullyRetrieved() {
        ScrapperClient scrapperClient = mock(ScrapperClient.class);
        Mockito.when(scrapperClient.getLinks()).thenReturn("/links/");

        ListLinkResponse expectedResponse = new ListLinkResponse(Collections.emptyList(), 0);
        when(scrapperClient.getAllLinks(123456789L)).thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(scrapperClient.getAllLinks(123456789L))
            .expectNext(expectedResponse)
            .expectComplete()
            .verify();
    }

    @Test
    @DisplayName("Add Link - Successfully Added")
    void addLink_SuccessfullyAdded() {
        ScrapperClient scrapperClient = mock(ScrapperClient.class);
        Mockito.when(scrapperClient.getLinks()).thenReturn("/links/");

        AddLinkRequest addLinkRequest = new AddLinkRequest(URI.create("example.com"));
        LinkResponse expectedResponse = new LinkResponse(
            URI.create("example.com"),
            OffsetDateTime.now()
        );
        when(scrapperClient.addLink(eq(123456789L), any(AddLinkRequest.class))).thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(scrapperClient.addLink(123456789L, addLinkRequest))
            .expectNext(expectedResponse)
            .expectComplete()
            .verify();
    }

    @Test
    @DisplayName("Remove Link - Successfully Removed")
    void removeLink_SuccessfullyRemoved() {
        ScrapperClient scrapperClient = mock(ScrapperClient.class);
        Mockito.when(scrapperClient.getLinks()).thenReturn("/links/");

        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(URI.create("example.com"));
        LinkResponse expectedResponse = new LinkResponse(
            URI.create("example.com"),
            OffsetDateTime.now()
        );
        when(scrapperClient.removeLink(eq(123456789L), any(RemoveLinkRequest.class))).thenReturn(Mono.just(
            expectedResponse));

        StepVerifier.create(scrapperClient.removeLink(123456789L, removeLinkRequest))
            .expectNext(expectedResponse)
            .expectComplete()
            .verify();
    }
}

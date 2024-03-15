package edu.java.scrapper.controller;

import edu.java.controller.WebServerExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = WebServerExceptionHandler.class)
class WebServerExceptionHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

//    @Test
//    @DisplayName("Should handle InternalServerScrapperException and return 500 status")
//    void handleInternalServerScrapperException() {
//        // Arrange
//        when(someService.registerChat(anyLong()))
//            .thenReturn(Mono.error(new InternalServerScrapperException(
//                "Internal server error",
//                "Внутреzнняя ошибка сервера"
//            )));
//
//        // Act & Assert
//        webTestClient.get()
//            .uri("/tg-chat/{id}", 123)
//            .exchange()
//            .expectStatus().is5xxServerError()
//            .expectBody(ApiErrorResponse.class)
//            .value(response -> {
//            });
//    }

    // Add similar test methods for other exception handlers (BadRequestScrapperException, NotFoundScrapperException, etc.)
}

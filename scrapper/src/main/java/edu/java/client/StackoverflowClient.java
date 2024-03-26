package edu.java.client;

import lombok.RequiredArgsConstructor;
import org.example.dto.StackoverflowQuestionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class StackoverflowClient {

    private final WebClient stackoverflowWebClient;
    @Value(value = "${app.stackoverflow-properties.url}")
    private String url;

    public Mono<StackoverflowQuestionResponse> fetchQuestion(long questionId) {
        String apiUrl = String.format(url, questionId);

        return stackoverflowWebClient
            .get()
            .uri(apiUrl)
            .retrieve()
            .bodyToMono(StackoverflowQuestionResponse.class);
    }
}

package edu.java.stackoverflow;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class StackoverflowClient {

    private static final String URL = "/questions/%d?order=%s&sort=%s&site=stackoverflow";
    private final WebClient webClient;

    public StackoverflowClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<QuestionResponse> fetchQuestion(long questionId, String sort, String order) {
        String apiUrl = String.format(URL, questionId, sort, order);

        return webClient
            .get()
            .uri(apiUrl)
            .retrieve()
            .bodyToMono(QuestionResponse.class);
    }
}

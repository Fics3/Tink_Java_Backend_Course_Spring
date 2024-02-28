package edu.java.client;

import edu.java.dto.StackoverflowQuestionResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class StackoverflowClient {

    private static final String URL = "/questions/%d?order=%s&sort=%s&site=stackoverflow";
    private final WebClient webClient;

    public StackoverflowClient(@Qualifier("stackoverflow") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<StackoverflowQuestionResponse> fetchQuestion(long questionId, String sort, String order) {
        String apiUrl = String.format(URL, questionId, sort, order);

        return webClient
            .get()
            .uri(apiUrl)
            .retrieve()
            .bodyToMono(StackoverflowQuestionResponse.class);
    }
}

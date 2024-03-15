package edu.java.client;

import edu.java.configuration.ApplicationConfig;
import lombok.AllArgsConstructor;
import org.example.dto.StackoverflowQuestionResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class StackoverflowClient {

    private final ApplicationConfig applicationConfig;
    private final WebClient stackoverflowWebClient;

    public Mono<StackoverflowQuestionResponse> fetchQuestion(long questionId, String sort, String order) {
        String apiUrl = String.format(applicationConfig.stackoverflowProperties().url(), questionId, sort, order);

        return stackoverflowWebClient
            .get()
            .uri(apiUrl)
            .retrieve()
            .bodyToMono(StackoverflowQuestionResponse.class);
    }
}

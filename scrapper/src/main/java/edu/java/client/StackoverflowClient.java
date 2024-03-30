package edu.java.client;

import edu.java.configuration.ApplicationConfig;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.example.dto.StackoverflowQuestionResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
@RequiredArgsConstructor
public class StackoverflowClient {

    private final ApplicationConfig applicationConfig;
    private final WebClient stackoverflowWebClient;
    private final Retry retry;

    public Mono<StackoverflowQuestionResponse> fetchQuestion(URI url) {
        String[] urlSplit = url.toString().split("/questions/");

        Integer questionId = Integer.parseInt(urlSplit[1].split("/")[0]);

        String apiUrl = String.format(applicationConfig.stackoverflowProperties().url(), questionId);

        return stackoverflowWebClient
            .get()
            .uri(apiUrl)
            .retrieve()
            .bodyToMono(StackoverflowQuestionResponse.class)
            .retryWhen(retry);
    }

}

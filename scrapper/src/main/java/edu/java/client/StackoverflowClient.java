package edu.java.client;

import edu.java.configuration.ApplicationConfig;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Objects;
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

    public Mono<StackoverflowQuestionResponse> fetchQuestion(Integer questionId) {
        String apiUrl = String.format(applicationConfig.stackoverflowProperties().url(), questionId);

        return stackoverflowWebClient
            .get()
            .uri(apiUrl)
            .retrieve()
            .bodyToMono(StackoverflowQuestionResponse.class);
    }

    public OffsetDateTime checkForUpdate(URI url) {
        String[] urlSplit = url.toString().split("/");

        Integer questionId = Integer.parseInt(urlSplit[urlSplit.length - 2]);

        var fetchedQuestion = fetchQuestion(questionId);

        return Objects.requireNonNull(fetchedQuestion.block()).items().getFirst().lastActivityDate();
    }
}

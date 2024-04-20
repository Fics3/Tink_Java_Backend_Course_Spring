package edu.java.client;

import edu.java.configuration.ClientConfig;
import edu.java.exception.BadRequestScrapperException;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.example.dto.StackoverflowQuestionResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import static edu.java.configuration.retry.RetryUtils.createRetry;

@Component
@RequiredArgsConstructor
public class StackoverflowClient {

    private final ClientConfig clientConfig;
    private final WebClient stackoverflowWebClient;

    public Mono<StackoverflowQuestionResponse> fetchQuestion(URI url) {
        Retry retry = createRetry(clientConfig.stackoverflowProperties().retryPolicy());

        String[] urlSplit = url.toString().split("/questions/");
        try {

            Integer questionId = Integer.parseInt(urlSplit[1].split("/")[0]);

            String apiUrl = String.format(clientConfig.stackoverflowProperties().questions(), questionId);

            return stackoverflowWebClient
                .get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(StackoverflowQuestionResponse.class)
                .retryWhen(retry);
        } catch (NumberFormatException numberFormatException) {
            throw new BadRequestScrapperException("Question is number", "");
        } catch (ArrayIndexOutOfBoundsException exception) {
            throw new BadRequestScrapperException("Not a question", "");
        }
    }

}

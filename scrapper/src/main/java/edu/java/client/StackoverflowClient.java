package edu.java.client;

import edu.java.configuration.ClientConfig;
import edu.java.configuration.retry.RetryBuilder;
import edu.java.configuration.retry.RetryPolicy;
import edu.java.exception.BadRequestScrapperException;
import java.net.URI;
import java.util.Map;
import org.example.dto.StackoverflowQuestionResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
public class StackoverflowClient {

    private final ClientConfig clientConfig;
    private final WebClient stackoverflowWebClient;
    @Qualifier("retryBuilder")
    private final Map<RetryPolicy.BackoffStrategy, RetryBuilder> retryBuilderMap;

    public StackoverflowClient(
        ClientConfig clientConfig,
        WebClient stackoverflowWebClient,
        Map<RetryPolicy.BackoffStrategy, RetryBuilder> retryBuilderMap
    ) {
        this.clientConfig = clientConfig;
        this.stackoverflowWebClient = stackoverflowWebClient;
        this.retryBuilderMap = retryBuilderMap;
    }

    public Mono<StackoverflowQuestionResponse> fetchQuestion(URI url) {
        Retry retry = retryBuilderMap.get(clientConfig.stackoverflowProperties().retryPolicy().getBackoffStrategy())
            .build(
                clientConfig.stackoverflowProperties().retryPolicy().getAttempts(),
                clientConfig.stackoverflowProperties().retryPolicy().getBackoff(),
                clientConfig.stackoverflowProperties().retryPolicy().getRetryStatusCodes()
            );
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

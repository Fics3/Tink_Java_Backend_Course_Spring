package edu.java.client;

import edu.java.configuration.ClientConfig;
import edu.java.configuration.retry.RetryBuilder;
import edu.java.configuration.retry.RetryPolicy;
import edu.java.exception.BadRequestScrapperException;
import java.net.URI;
import java.util.Map;
import org.example.dto.GithubRepositoryResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
public class GithubClient {

    private final ClientConfig clientConfig;
    private final WebClient githubWebClient;
    @Qualifier("retryBuilder")
    private final Map<RetryPolicy.BackoffStrategy, RetryBuilder> retryBuilderMap;

    public GithubClient(
        ClientConfig clientConfig,
        WebClient githubWebClient,
        Map<RetryPolicy.BackoffStrategy, RetryBuilder> retryBuilderMap
    ) {
        this.clientConfig = clientConfig;
        this.githubWebClient = githubWebClient;
        this.retryBuilderMap = retryBuilderMap;
    }

    public Mono<GithubRepositoryResponse> fetchRepository(URI url) {
        String[] urlSplit = url.getPath().split("/");
        Retry retry = retryBuilderMap.get(clientConfig.githubProperties().retryPolicy().getBackoffStrategy())
            .build(
                clientConfig.githubProperties().retryPolicy().getAttempts(),
                clientConfig.githubProperties().retryPolicy().getBackoff(),
                clientConfig.githubProperties().retryPolicy().getRetryStatusCodes()
            );

        try {
            String owner = urlSplit[1];
            String repo = urlSplit[2];
            String apiUrl = String.format(clientConfig.githubProperties().repos(), owner, repo);

            return githubWebClient
                .get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(GithubRepositoryResponse.class)
                .retryWhen(retry);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new BadRequestScrapperException("Неправильный тип ссылки", "");
        }
    }

}

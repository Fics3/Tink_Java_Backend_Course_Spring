package edu.java.client;

import edu.java.configuration.ApplicationConfig;
import edu.java.exception.BadRequestScrapperException;
import java.net.URI;
import lombok.AllArgsConstructor;
import org.example.dto.GithubRepositoryResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
@AllArgsConstructor
public class GithubClient {

    private final ApplicationConfig applicationConfig;
    private final WebClient githubWebClient;
    private final Retry retry;

    public Mono<GithubRepositoryResponse> fetchRepository(URI url) {
        String[] urlSplit = url.getPath().split("/");
        try {
            String apiUrl = String.format(applicationConfig.githubProperties().url(), urlSplit[1], urlSplit[2]);
            String owner = urlSplit[1];
            String repo = urlSplit[2];

            String apiUrl = String.format(applicationConfig.githubProperties().repos(), owner, repo);

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

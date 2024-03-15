package edu.java.client;

import edu.java.configuration.ApplicationConfig;
import lombok.AllArgsConstructor;
import org.example.dto.GithubRepositoryResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Objects;

@Component
@AllArgsConstructor
public class GithubClient {

    private final ApplicationConfig applicationConfig;
    private final WebClient githubWebClient;

    public Mono<GithubRepositoryResponse> fetchRepository(String owner, String repo) {
        String apiUrl = String.format(applicationConfig.githubProperties().url(), owner, repo);

        return githubWebClient
            .get()
            .uri(apiUrl)
            .retrieve()
            .bodyToMono(GithubRepositoryResponse.class);
    }

    public OffsetDateTime checkForUpdate(URI url) {
        String[] urlSplit = url.getPath().split("/");

        var fetchedRepo = fetchRepository(urlSplit[1], urlSplit[2]);

        return Objects.requireNonNull(fetchedRepo.block()).updatedAt();
    }
}

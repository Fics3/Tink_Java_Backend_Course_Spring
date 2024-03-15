package edu.java.client;

import edu.java.configuration.ApplicationConfig;
import lombok.AllArgsConstructor;
import org.example.dto.GithubRepositoryResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

    public void checkForUpdate() {
        
    }
}

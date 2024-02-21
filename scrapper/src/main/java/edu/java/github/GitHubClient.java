package edu.java.github;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GitHubClient {

    private final WebClient webClient;

    public GitHubClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<RepositoryResponse> fetchRepository(String owner, String repo) {
        String apiUrl = String.format("/repos/%s/%s", owner, repo);

        return webClient
            .get()
            .uri(apiUrl)
            .retrieve()
            .bodyToMono(RepositoryResponse.class);
    }
}

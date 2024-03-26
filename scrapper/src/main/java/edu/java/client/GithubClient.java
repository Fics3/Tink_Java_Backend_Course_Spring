package edu.java.client;

import org.example.dto.GithubRepositoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class GithubClient {

    private static final String REPOS_PATH = "/repos/%s/%s";

    private final WebClient webClient;

    @Autowired
    public GithubClient(WebClient githubWebClient) {
        this.webClient = githubWebClient;
    }

    public Mono<GithubRepositoryResponse> fetchRepository(String owner, String repo) {
        String apiUrl = String.format(REPOS_PATH, owner, repo);

        return webClient
            .get()
            .uri(apiUrl)
            .retrieve()
            .bodyToMono(GithubRepositoryResponse.class);
    }
}

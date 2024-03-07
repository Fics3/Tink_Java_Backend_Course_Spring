package edu.java.client;

import edu.java.dto.GithubRepositoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class GithubWebClient {

    private static final String REPOS_PATH = "/repos/%s/%s";

    private final WebClient webClient;

    @Autowired
    public GithubWebClient(WebClient githubClient) {
        this.webClient = githubClient;
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

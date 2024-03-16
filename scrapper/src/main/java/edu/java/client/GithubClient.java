package edu.java.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.dto.GithubRepositoryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Getter
public class GithubClient {

    private final WebClient githubWebClient;
    @Value("${app.github-properties.url}")
    private String url;

    public Mono<GithubRepositoryResponse> fetchRepository(String owner, String repo) {
        String apiUrl = String.format(url, owner, repo);

        return githubWebClient
            .get()
            .uri(apiUrl)
            .retrieve()
            .bodyToMono(GithubRepositoryResponse.class);
    }
}

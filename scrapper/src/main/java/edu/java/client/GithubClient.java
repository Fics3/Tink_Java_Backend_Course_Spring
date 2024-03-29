package edu.java.client;

import edu.java.configuration.ApplicationConfig;
import edu.java.exception.BadRequestScrapperException;
import java.net.URI;
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

    public Mono<GithubRepositoryResponse> fetchRepository(URI url) {
        String[] urlSplit = url.getPath().split("/");
        try {
            String apiUrl = String.format(applicationConfig.githubProperties().apiUrl(), urlSplit[1], urlSplit[2]);

            return githubWebClient
                .get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(GithubRepositoryResponse.class);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new BadRequestScrapperException("Неправильный тип ссылки", "");
        }
    }

}

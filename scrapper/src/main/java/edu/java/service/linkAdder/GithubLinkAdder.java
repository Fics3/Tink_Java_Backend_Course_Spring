package edu.java.service.linkAdder;

import edu.java.client.GithubClient;
import edu.java.configuration.ApplicationConfig;
import edu.java.domain.repository.LinksRepository;
import edu.java.model.LinkModel;
import java.net.URI;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GithubLinkAdder implements LinkAdder {

    private final ApplicationConfig applicationConfig;
    private final GithubClient githubClient;
    private final LinksRepository jooqLinksRepository;

    @Override
    public LinkModel addLink(URI url, Long tgChatId) {
        var repository = githubClient.fetchRepository(url).block();
        return jooqLinksRepository.addRepository(
            tgChatId,
            url.toString(),
            Objects.requireNonNull(repository).updatedAt(),
            repository.subscribersCount()
        );
    }

    @Override
    public String getDomain() {
        return applicationConfig.githubProperties().domain();
    }
}

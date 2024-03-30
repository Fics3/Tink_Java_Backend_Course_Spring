package edu.java.service.linkAdder;

import edu.java.client.GithubClient;
import edu.java.configuration.ApplicationConfig;
import edu.java.domain.repository.GithubRepositoryRepository;
import edu.java.domain.repository.LinksRepository;
import edu.java.exception.BadRequestScrapperException;
import edu.java.model.LinkModel;
import java.net.URI;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GithubLinkAdder implements LinkAdder {

    private final ApplicationConfig applicationConfig;
    private final GithubClient githubClient;
    private final LinksRepository linksRepository;
    private final GithubRepositoryRepository githubRepositoryRepository;

    @Override
    public LinkModel addLink(URI url, Long tgChatId) {
        var repository = githubClient.fetchRepository(url).block();
        if (repository != null && repository.name() == null) {
            throw new BadRequestScrapperException("Внутрение проблемы", "Подождите");
        }
        var link = linksRepository.addLink(
            tgChatId,
            url.toString(),
            Objects.requireNonNull(repository).pushedAt()
        );
        return githubRepositoryRepository.addRepository(
            link,
            repository.subscribersCount()
        );
    }

    @Override
    public String getDomain() {
        return applicationConfig.githubProperties().domain();
    }
}

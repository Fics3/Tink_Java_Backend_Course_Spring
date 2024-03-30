package edu.java.service.updateChecker;

import edu.java.client.BotClient;
import edu.java.client.GithubClient;
import edu.java.configuration.ApplicationConfig;
import edu.java.domain.repository.ChatRepository;
import edu.java.domain.repository.GithubRepositoryRepository;
import edu.java.domain.repository.LinksRepository;
import edu.java.model.LinkModel;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.LinkUpdateRequest;

@RequiredArgsConstructor
public class GithubUpdateChecker implements UpdateChecker {

    private final ApplicationConfig applicationConfig;
    private final GithubClient githubClient;
    private final BotClient botClient;
    private final ChatRepository jooqChatRepository;
    private final LinksRepository jooqLinksRepository;
    private final GithubRepositoryRepository jooqGithubRepositoryRepository;

    @Override
    public int processUrlUpdates(LinkModel linkModel, int updateCount) {
        var repository = githubClient.fetchRepository(URI.create(linkModel.link())).block();
        processLastUpdate(linkModel, updateCount, Objects.requireNonNull(repository).updatedAt());
        processSubscriberCount(linkModel, repository.subscribersCount());
        return updateCount;
    }

    private void processLastUpdate(LinkModel linkModel, int updateCount, OffsetDateTime lastUpdate) {
        var url = URI.create(linkModel.link());
        int tmpCount = updateCount;
        if (lastUpdate != null && lastUpdate.isAfter(linkModel.lastUpdate())) {
            botClient.sendUpdate(formLinkUpdateRequest(
                linkModel.linkId(),
                url,
                "Ссылка обновлена " + linkModel.link()
            )).subscribe();
            tmpCount++;
            jooqLinksRepository.updateLastUpdate(linkModel.linkId(), lastUpdate);
        }
        jooqLinksRepository.updateChecked(linkModel.linkId(), OffsetDateTime.now());
    }

    private void processSubscriberCount(LinkModel linkModel, Integer subscribersCount) {
        var repositoryModel = jooqGithubRepositoryRepository.getRepositoryByLinkId(linkModel.linkId());
        if (repositoryModel == null) {
            return;
        }
        if (subscribersCount != null
            && !subscribersCount.equals(jooqGithubRepositoryRepository.getRepositoryByLinkId(linkModel.linkId())
            .subscribersCount())) {
            botClient.sendUpdate(formLinkUpdateRequest(
                linkModel.linkId(),
                URI.create(linkModel.link()),
                "Число подписчиков изменилось, теперь: " + subscribersCount
            )).subscribe();
        }
    }

    private LinkUpdateRequest formLinkUpdateRequest(UUID linkId, URI url, String description) {
        return new LinkUpdateRequest(
            linkId,
            url,
            description,
            jooqChatRepository.findChatsByLinkId(linkId)
        );
    }

    @Override
    public String getDomain() {
        return applicationConfig.githubProperties().domain();
    }
}

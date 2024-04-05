package edu.java.service.updateChecker;

import edu.java.client.GithubClient;
import edu.java.configuration.ClientConfig;
import edu.java.domain.repository.ChatRepository;
import edu.java.domain.repository.GithubRepositoryRepository;
import edu.java.domain.repository.LinksRepository;
import edu.java.model.LinkModel;
import edu.java.service.NotificationService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.LinkUpdateRequest;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class GithubUpdateChecker implements UpdateChecker {

    private final ClientConfig clientConfig;
    private final GithubClient githubClient;
    private final NotificationService notificationService;
    private final ChatRepository chatRepository;
    private final LinksRepository linksRepository;
    private final GithubRepositoryRepository githubRepositoryRepository;

    @Override
    @Transactional
    public int processUrlUpdates(LinkModel linkModel, int updateCount) {
        var repository = githubClient.fetchRepository(URI.create(linkModel.link())).block();
        processLastUpdate(linkModel, updateCount, Objects.requireNonNull(repository).pushedAt());
        processSubscriberCount(linkModel, repository.subscribersCount());
        return updateCount;
    }

    private void processLastUpdate(LinkModel linkModel, int updateCount, OffsetDateTime lastUpdate) {
        var url = URI.create(linkModel.link());
        int tmpCount = updateCount;
        if (lastUpdate != null && lastUpdate.isAfter(linkModel.lastUpdate())) {
            notificationService.sendNotification(formLinkUpdateRequest(
                linkModel.linkId(),
                url,
                "Ссылка обновлена " + linkModel.link()
            ));
            tmpCount++;
            linksRepository.updateLastUpdate(linkModel.linkId(), lastUpdate);
        }
        linksRepository.updateChecked(linkModel.linkId(), OffsetDateTime.now());
    }

    private void processSubscriberCount(LinkModel linkModel, Integer subscribersCount) {
        var repositoryModel = githubRepositoryRepository.getRepositoryByLinkId(linkModel.linkId());
        if (repositoryModel == null) {
            return;
        }
        if (subscribersCount != null
            && !subscribersCount.equals(githubRepositoryRepository.getRepositoryByLinkId(linkModel.linkId())
            .subscribersCount())) {
            notificationService.sendNotification(formLinkUpdateRequest(
                linkModel.linkId(),
                URI.create(linkModel.link()),
                "Число подписчиков изменилось, теперь: " + subscribersCount
            ));
        }
    }

    private LinkUpdateRequest createLinkUpdateRequest(UUID linkId, URI url, String description) {
        return new LinkUpdateRequest(
            linkId,
            url,
            description,
            chatRepository.findChatsByLinkId(linkId)
        );
    }

    @Override
    public String getDomain() {
        return clientConfig.githubProperties().domain();
    }
}

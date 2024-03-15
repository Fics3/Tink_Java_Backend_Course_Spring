package edu.java.service.jdbc;

import edu.java.client.BotClient;
import edu.java.client.GithubClient;
import edu.java.client.StackoverflowClient;
import edu.java.repository.ChatRepository;
import edu.java.repository.LinksRepository;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;
import edu.java.service.LinkUpdater;
import lombok.AllArgsConstructor;
import org.example.dto.LinkUpdateRequest;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JdbcLinkUpdater implements LinkUpdater {

    private LinksRepository linksRepository;
    private ChatRepository chatRepository;
    private GithubClient githubClient;
    private StackoverflowClient stackoverflowClient;
    private BotClient botClient;

    @Override
    public int update() {
        int updateCount = 0;
        var staleLinks = linksRepository.findStaleLinks(Duration.ofSeconds(5L));
        for (var linkModel : staleLinks) {
            OffsetDateTime lastUpdate = null;
            var url = URI.create(linkModel.link());
            switch (url.getHost()) {
                case "github.com" -> lastUpdate = githubClient.checkForUpdate(url);
                case "stackoverflow.com" -> lastUpdate = stackoverflowClient.checkForUpdate(url);
            }

            if (lastUpdate != null && lastUpdate.isAfter(linkModel.lastUpdate())) {
                botClient.sendUpdate(formLinkUpdateRequest(
                    linkModel.linkId(),
                    url,
                    "Ссылка обновлена " + linkModel.link()
                )).subscribe();
                updateCount++;
                linksRepository.updateCheckedAndLastUpdate(linkModel.linkId(), lastUpdate, OffsetDateTime.now());
            }

        }
        return updateCount;
    }

    private LinkUpdateRequest formLinkUpdateRequest(UUID linkId, URI url, String description) {
        return new LinkUpdateRequest(
            linkId,
            url,
            description,
            chatRepository.findChatsByLinkId(linkId)
        );
    }
}

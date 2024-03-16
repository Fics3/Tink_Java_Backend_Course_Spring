package edu.java.service.jdbc;

import edu.java.client.BotClient;
import edu.java.client.GithubClient;
import edu.java.client.StackoverflowClient;
import edu.java.repository.ChatRepository;
import edu.java.repository.LinksRepository;
import edu.java.service.LinkUpdater;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.dto.LinkUpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class JdbcLinkUpdater implements LinkUpdater {
    private final LinksRepository linksRepository;
    private final ChatRepository chatRepository;
    private final GithubClient githubClient;
    private final StackoverflowClient stackoverflowClient;
    private final BotClient botClient;
    @Value("#{@scheduler.forceCheckDelay().toMillis()}")
    private Duration threshold;

    @Override
    public int update() {
        int updateCount = 0;
        var staleLinks = linksRepository.findStaleLinks(threshold);
        for (var linkModel : staleLinks) {
            var url = URI.create(linkModel.link());
            OffsetDateTime lastUpdate = getLastUpdate(url);
            if (lastUpdate != null && lastUpdate.isAfter(linkModel.lastUpdate())) {
                botClient.sendUpdate(formLinkUpdateRequest(
                    linkModel.linkId(),
                    url,
                    "Ссылка обновлена " + linkModel.link()
                )).subscribe();
                updateCount++;
                linksRepository.updateLastUpdate(linkModel.linkId(), lastUpdate);
            }
            linksRepository.updateChecked(linkModel.linkId(), OffsetDateTime.now());
        }
        return updateCount;
    }

    private OffsetDateTime getLastUpdate(URI url) {
        return switch (url.getHost()) {
            case "github.com" -> githubClient.checkForUpdate(url);
            case "stackoverflow.com" -> stackoverflowClient.checkForUpdate(url);
            default -> null;
        };
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

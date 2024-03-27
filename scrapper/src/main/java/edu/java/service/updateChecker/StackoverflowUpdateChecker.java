package edu.java.service.updateChecker;

import edu.java.client.BotClient;
import edu.java.client.StackoverflowClient;
import edu.java.configuration.ApplicationConfig;
import edu.java.model.LinkModel;
import edu.java.repository.ChatRepository;
import edu.java.repository.LinksRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.LinkUpdateRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StackoverflowUpdateChecker implements UpdateChecker {
    private final ApplicationConfig applicationConfig;
    private final StackoverflowClient stackoverflowClient;
    private final BotClient botClient;
    private final ChatRepository chatRepository;
    private final LinksRepository linksRepository;

    @Override
    public void processUrlUpdates(LinkModel linkModel, int updateCount) {
        var question = stackoverflowClient.fetchQuestion(URI.create(linkModel.link())).block();
        processLastUpdate(
            linkModel,
            updateCount,
            Objects.requireNonNull(question).items().getFirst().lastActivityDate()
        );
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
            linksRepository.updateLastUpdate(linkModel.linkId(), lastUpdate);
        }
    }

    private LinkUpdateRequest formLinkUpdateRequest(UUID linkId, URI url, String description) {
        return new LinkUpdateRequest(
            linkId,
            url,
            description,
            chatRepository.findChatsByLinkId(linkId)
        );
    }

    @Override
    public String getDomain() {
        return applicationConfig.stackoverflowProperties().domain();
    }
}

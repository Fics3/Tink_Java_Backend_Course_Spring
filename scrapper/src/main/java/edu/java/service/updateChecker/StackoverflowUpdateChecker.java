package edu.java.service.updateChecker;

import edu.java.client.BotClient;
import edu.java.client.StackoverflowClient;
import edu.java.configuration.ClientConfig;
import edu.java.domain.repository.ChatRepository;
import edu.java.domain.repository.LinksRepository;
import edu.java.domain.repository.StackoverflowQuestionRepository;
import edu.java.model.LinkModel;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.LinkUpdateRequest;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class StackoverflowUpdateChecker implements UpdateChecker {
    private final ClientConfig clientConfig;
    private final StackoverflowClient stackoverflowClient;
    private final BotClient botClient;
    private final ChatRepository chatRepository;
    private final LinksRepository linksRepository;
    private final StackoverflowQuestionRepository jooqStackoverflowQuestionRepository;

    @Override
    @Transactional
    public int processUrlUpdates(LinkModel linkModel, int updateCount) {
        var question = stackoverflowClient.fetchQuestion(URI.create(linkModel.link())).block();
        processLastUpdate(
            linkModel,
            updateCount,
            Objects.requireNonNull(question).items().getFirst().lastActivityDate()
        );
        processAnswerCount(linkModel, question.items().getFirst().answerCount());
        return updateCount;
    }

    private void processLastUpdate(LinkModel linkModel, int updateCount, OffsetDateTime lastUpdate) {
        var url = URI.create(linkModel.link());
        int tmpCount = updateCount;
        if (lastUpdate != null && lastUpdate.isAfter(linkModel.lastUpdate())) {
            botClient.sendUpdate(createLinkUpdateRequest(
                linkModel.linkId(),
                url,
                "Ссылка обновлена " + linkModel.link()
            )).subscribe();
            tmpCount++;
            linksRepository.updateLastUpdate(linkModel.linkId(), lastUpdate);
        }
    }

    private void processAnswerCount(LinkModel linkModel, Integer answerCount) {
        var url = URI.create(linkModel.link());
        var questionModel = jooqStackoverflowQuestionRepository.getQuestionByLinkId(linkModel.linkId());
        if (questionModel == null) {
            return;
        }
        var questionCount = questionModel.answerCount();
        if (answerCount != null && !questionCount.equals(answerCount)) {
            botClient.sendUpdate(createLinkUpdateRequest(
                linkModel.linkId(),
                url,
                "Количество ответов обновилось: " + answerCount
            )).subscribe();
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
        return clientConfig.stackoverflowProperties().domain();
    }
}

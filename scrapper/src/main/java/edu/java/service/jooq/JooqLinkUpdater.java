package edu.java.service.jooq;

import edu.java.client.BotClient;
import edu.java.client.GithubClient;
import edu.java.client.StackoverflowClient;
import edu.java.domain.repository.jooq.JooqChatRepository;
import edu.java.domain.repository.jooq.JooqLinksRepository;
import edu.java.domain.repository.jooq.JooqQuestionRepository;
import edu.java.domain.repository.jooq.JooqRepositoryRepository;
import edu.java.exception.InternalServerScrapperException;
import edu.java.model.LinkModel;
import edu.java.service.LinkUpdater;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.dto.GithubRepositoryResponse;
import org.example.dto.LinkUpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class JooqLinkUpdater implements LinkUpdater {
    private final JooqLinksRepository jooqLinksRepository;
    private final JooqChatRepository jooqChatRepository;
    private final GithubClient githubClient;
    private final StackoverflowClient stackoverflowClient;
    private final BotClient botClient;
    private final JooqRepositoryRepository jooqRepositoryRepository;
    private final JooqQuestionRepository jooqQuestionRepository;
    @Value("#{@scheduler.forceCheckDelay().toMillis()}")
    private Duration threshold;

    @Override
    public int update() {
        int updateCount = 0;
        var staleLinks = jooqLinksRepository.findStaleLinks(threshold);
        for (var linkModel : staleLinks) {
            var url = URI.create(linkModel.link());
            switch (url.getHost()) {
                case "github.com" -> {
                    GithubRepositoryResponse repositoryResponse = githubClient.fetchRepository(url).block();
                    processSubscriberCount(linkModel, Objects.requireNonNull(repositoryResponse).subscribersCount());
                    processLastUpdate(linkModel, updateCount, Objects.requireNonNull(repositoryResponse).pushedAt());
                }
                case "stackoverflow.com" -> {
                    var question = stackoverflowClient.fetchQuestion(url).block();
                    processAnswerCount(linkModel, Objects.requireNonNull(question).items().getFirst().answerCount());
                    processLastUpdate(
                        linkModel,
                        updateCount,
                        Objects.requireNonNull(question).items().getFirst().lastActivityDate()
                    );
                }
                default -> throw new InternalServerScrapperException(
                    "Ссылка не поддерживается",
                    "Только гит и стековерфлоу("
                );
            }
            jooqLinksRepository.updateChecked(linkModel.linkId(), OffsetDateTime.now());
        }
        return updateCount;
    }

    private void processSubscriberCount(LinkModel linkModel, Integer subscribersCount) {
        var repositoryModel = jooqRepositoryRepository.getRepositoryByLinkId(linkModel.linkId());
        if (repositoryModel == null) {
            return;
        }
        if (subscribersCount != null
            && !subscribersCount.equals(jooqRepositoryRepository.getRepositoryByLinkId(linkModel.linkId())
            .subscribersCount())) {
            botClient.sendUpdate(formLinkUpdateRequest(
                linkModel.linkId(),
                URI.create(linkModel.link()),
                "Число подписчиков изменилось, теперь: " + subscribersCount
            )).subscribe();
            jooqRepositoryRepository.updateSubscribersCount(linkModel.linkId(), subscribersCount);
        }
    }

    private void processAnswerCount(LinkModel linkModel, Integer answerCount) {
        var url = URI.create(linkModel.link());
        var questionModel = jooqQuestionRepository.getQuestionByLinkId(linkModel.linkId());
        if (questionModel == null) {
            return;
        }
        var questionCount = questionModel.answerCount();
        if (answerCount != null && !questionCount.equals(answerCount)) {
            botClient.sendUpdate(formLinkUpdateRequest(
                linkModel.linkId(),
                url,
                "Количество ответов обновилось: " + answerCount
            )).subscribe();
            jooqQuestionRepository.updateAnswerCount(linkModel.linkId(), answerCount);
        }
    }

    private void processLastUpdate(LinkModel linkModel, int updateCount, OffsetDateTime lastUpdate) {
        var url = URI.create(linkModel.link());
        var tmpCount = updateCount;
        if (lastUpdate != null && lastUpdate.isAfter(linkModel.lastUpdate())) {
            botClient.sendUpdate(formLinkUpdateRequest(
                linkModel.linkId(),
                url,
                "Обновление было в: " + lastUpdate
            )).subscribe();
            tmpCount++;
            jooqLinksRepository.updateLastUpdate(linkModel.linkId(), lastUpdate);
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
}

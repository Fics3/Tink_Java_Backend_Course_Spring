package edu.java.service.jpa;

import edu.java.client.BotClient;
import edu.java.client.GithubClient;
import edu.java.client.StackoverflowClient;
import edu.java.domain.entity.ChatEntity;
import edu.java.domain.entity.LinkEntity;
import edu.java.domain.repository.jpa.JpaLinksRepository;
import edu.java.domain.repository.jpa.JpaQuestionRepository;
import edu.java.domain.repository.jpa.JpaRepositoryRepository;
import edu.java.exception.InternalServerScrapperException;
import edu.java.service.LinkUpdater;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.GithubRepositoryResponse;
import org.example.dto.LinkUpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RequiredArgsConstructor
public class JpaLinkUpdater implements LinkUpdater {
    private final JpaLinksRepository jpaLinksRepository;
    private final GithubClient githubClient;
    private final StackoverflowClient stackoverflowClient;
    private final BotClient botClient;
    private final JpaRepositoryRepository jpaRepositoryRepository;
    private final JpaQuestionRepository jpaQuestionRepository;
    @Value("#{@scheduler.forceCheckDelay().toMillis()}")
    private Duration threshold;

    @Override
    @Transactional
    public int update() {
        int updateCount = 0;
        var staleLinks = jpaLinksRepository.findByLastCheckAfter(OffsetDateTime.now().minus(threshold));
        for (var linkEntity : staleLinks) {
            var url = URI.create(linkEntity.getLink());
            try {
                switch (url.getHost()) {
                    case "github.com" -> {
                        GithubRepositoryResponse repositoryResponse = githubClient.fetchRepository(url).block();
                        processSubscriberCount(
                            linkEntity,
                            Objects.requireNonNull(repositoryResponse).subscribersCount()
                        );
                        processLastUpdate(
                            linkEntity,
                            updateCount,
                            Objects.requireNonNull(repositoryResponse).pushedAt()
                        );
                    }
                    case "stackoverflow.com" -> {
                        var question = stackoverflowClient.fetchQuestion(url).block();
                        processAnswerCount(
                            linkEntity,
                            Objects.requireNonNull(question).items().getFirst().answerCount()
                        );
                        processLastUpdate(
                            linkEntity,
                            updateCount,
                            Objects.requireNonNull(question).items().getFirst().lastActivityDate()
                        );
                    }
                    default -> throw new InternalServerScrapperException(
                        "Ссылка не поддерживается",
                        "Только гит и стековерфлоу("
                    );
                }
                jpaLinksRepository.updateChecked(linkEntity.getLinkId(), OffsetDateTime.now());

            } catch (WebClientResponseException e) {
                if (e.getStatusCode().isSameCodeAs(HttpStatus.FORBIDDEN)) {
                    jpaLinksRepository.updateChecked(linkEntity.getLinkId(), OffsetDateTime.now());
                }
            }
        }
        return updateCount;
    }

    private void processSubscriberCount(LinkEntity linkEntity, Integer subscribersCount) {
        var repositoryModel = jpaRepositoryRepository.findByLink(linkEntity);
        if (repositoryModel == null) {
            return;
        }
        if (subscribersCount != null
            && !subscribersCount.equals(repositoryModel.getSubscribersCount())) {
            botClient.sendUpdate(formLinkUpdateRequest(
                linkEntity.getLinkId(),
                URI.create(linkEntity.getLink()),
                "Число подписчиков изменилось, теперь: " + subscribersCount
            )).subscribe();
            jpaRepositoryRepository.updateSubscribersCount(linkEntity.getLinkId(), subscribersCount);
        }
    }

    private void processAnswerCount(LinkEntity linkEntity, Integer answerCount) {
        var url = URI.create(linkEntity.getLink());
        var questionModel = jpaQuestionRepository.findByLink(linkEntity);
        if (questionModel == null) {
            return;
        }
        var questionCount = questionModel.getAnswerCount();
        if (answerCount != null && !questionCount.equals(answerCount)) {
            botClient.sendUpdate(formLinkUpdateRequest(
                linkEntity.getLinkId(),
                url,
                "Количество ответов обновилось: " + answerCount
            )).subscribe();
            jpaQuestionRepository.updateAnswerCount(linkEntity.getLinkId(), answerCount);
        }
    }

    private void processLastUpdate(LinkEntity linkEntity, int updateCount, OffsetDateTime lastUpdate) {
        var url = URI.create(linkEntity.getLink());
        var tmpCount = updateCount;
        if (lastUpdate != null && lastUpdate.isAfter(linkEntity.getLastUpdate())) {
            botClient.sendUpdate(formLinkUpdateRequest(
                linkEntity.getLinkId(),
                url,
                "Обновление было в: " + lastUpdate
            )).subscribe();
            tmpCount++;
            jpaLinksRepository.updateLastUpdate(linkEntity.getLinkId(), lastUpdate);
        }
    }

    private LinkUpdateRequest formLinkUpdateRequest(UUID linkId, URI url, String description) {
        return new LinkUpdateRequest(
            linkId,
            url,
            description,
            jpaLinksRepository.findByLinkId(linkId)
                .getChats()
                .stream()
                .mapToLong(ChatEntity::getTelegramChatId)
                .boxed().toList()
        );
    }
}

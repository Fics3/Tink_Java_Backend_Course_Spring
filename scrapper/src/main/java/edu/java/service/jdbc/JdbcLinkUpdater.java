package edu.java.service.jdbc;

import edu.java.client.BotClient;
import edu.java.client.GithubClient;
import edu.java.client.StackoverflowClient;
import edu.java.domain.repository.jdbc.JdbcChatRepository;
import edu.java.domain.repository.jdbc.JdbcLinksRepository;
import edu.java.domain.repository.jdbc.JdbcQuestionRepository;
import edu.java.domain.repository.jdbc.JdbcRepositoryRepository;
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
public class JdbcLinkUpdater implements LinkUpdater {
    private final JdbcLinksRepository jdbcLinksRepository;
    private final JdbcChatRepository jdbcChatRepository;
    private final GithubClient githubClient;
    private final StackoverflowClient stackoverflowClient;
    private final BotClient botClient;
    private final JdbcRepositoryRepository repository;
    private final JdbcQuestionRepository jdbcQuestionRepository;
    @Value("#{@scheduler.forceCheckDelay().toMillis()}")
    private Duration threshold;

    @Override
    public int update() {
        int updateCount = 0;
        var staleLinks = jdbcLinksRepository.findStaleLinks(threshold);
        for (var linkModel : staleLinks) {
            var url = URI.create(linkModel.link());
            switch (url.getHost()) {
                case "github.com" -> {
                    GithubRepositoryResponse repository = githubClient.fetchRepository(url).block();
                    processSubscriberCount(linkModel, Objects.requireNonNull(repository).subscribersCount());
                    processLastUpdate(linkModel, updateCount, Objects.requireNonNull(repository).updatedAt());
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
            jdbcLinksRepository.updateChecked(linkModel.linkId(), OffsetDateTime.now());
        }
        return updateCount;
    }

    private void processSubscriberCount(LinkModel linkModel, Integer subscribersCount) {
        var repositoryModel = repository.getRepositoryByLinkId(linkModel.linkId());
        if (repositoryModel == null) {
            return;
        }
        if (subscribersCount != null
            && !subscribersCount.equals(repository.getRepositoryByLinkId(linkModel.linkId()).subscribersCount())) {
            botClient.sendUpdate(formLinkUpdateRequest(
                linkModel.linkId(),
                URI.create(linkModel.link()),
                "Число подписчиков изменилось, теперь: " + subscribersCount
            )).subscribe();
        }
    }

    private void processAnswerCount(LinkModel linkModel, Integer answerCount) {
        var url = URI.create(linkModel.link());
        var questionModel = jdbcQuestionRepository.getQuestionByLinkId(linkModel.linkId());
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
        }
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
            jdbcLinksRepository.updateLastUpdate(linkModel.linkId(), lastUpdate);
        }
    }

    private LinkUpdateRequest formLinkUpdateRequest(UUID linkId, URI url, String description) {
        return new LinkUpdateRequest(
            linkId,
            url,
            description,
            jdbcChatRepository.findChatsByLinkId(linkId)
        );
    }
}

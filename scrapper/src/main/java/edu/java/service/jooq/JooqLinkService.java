package edu.java.service.jooq;

import edu.java.client.GithubClient;
import edu.java.client.StackoverflowClient;
import edu.java.domain.repository.jooq.JooqLinksRepository;
import edu.java.exception.DuplicateLinkScrapperException;
import edu.java.model.LinkModel;
import edu.java.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.dto.LinkResponse;

@RequiredArgsConstructor
public class JooqLinkService implements LinkService {

    private final GithubClient githubClient;
    private final StackoverflowClient stackoverflowClient;
    private final JooqLinksRepository jooqLinksRepository;

    @Override
    public LinkModel add(Long tgChatId, URI url) {
        if (jooqLinksRepository.existsLinkForChat(tgChatId, url.toString())) {
            throw new DuplicateLinkScrapperException("Ссылка уже существует", url + "уже отсвеживается");
        }
        switch (url.getHost()) {
            case "stackoverflow.com" -> {
                var question = stackoverflowClient.fetchQuestion(url).block();
                return jooqLinksRepository.addQuestion(
                    tgChatId,
                    url.toString(),
                    Objects.requireNonNull(question).items().getFirst().lastActivityDate(),
                    question.items().getFirst().answerCount()
                );
            }
            case "github.com" -> {
                var repository = githubClient.fetchRepository(url).block();
                return jooqLinksRepository.addRepository(
                    tgChatId,
                    url.toString(),
                    Objects.requireNonNull(repository).pushedAt(),
                    repository.subscribersCount()
                );
            }
            default -> {
                return jooqLinksRepository.addLink(tgChatId, url.toString(), OffsetDateTime.now());
            }
        }
    }

    @Override public LinkModel remove(Long tgChatId, URI url) {
        return jooqLinksRepository.removeLink(tgChatId, url.toString());
    }

    @Override public List<LinkResponse> findAll(Long tgChatId) {
        return jooqLinksRepository.findLinksByChatId(tgChatId).stream()
            .map(linkModel -> new LinkResponse(
                URI.create(linkModel.link()),
                linkModel.lastUpdate()
            ))
            .collect(Collectors.toList());
    }
}

package edu.java.service.jdbc;

import edu.java.client.GithubClient;
import edu.java.client.StackoverflowClient;
import edu.java.domain.repository.jdbc.JdbcLinksRepository;
import edu.java.exception.DuplicateLinkScrapperException;
import edu.java.model.LinkModel;
import edu.java.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.example.dto.LinkResponse;

@AllArgsConstructor
public class JdbcLinkService implements LinkService {

    private JdbcLinksRepository jdbcLinksRepository;
    private GithubClient githubClient;
    private StackoverflowClient stackoverflowClient;

    @Override
    public LinkModel add(Long tgChatId, URI url) {
        if (jdbcLinksRepository.existsLinkForChat(tgChatId, url.toString())) {
            throw new DuplicateLinkScrapperException("Ссылка уже существует", url + "уже отсвеживается");
        }
        switch (url.getHost()) {
            case "stackoverflow.com" -> {
                var question = stackoverflowClient.fetchQuestion(url).block();
                return jdbcLinksRepository.addQuestion(
                    tgChatId,
                    url.toString(),
                    Objects.requireNonNull(question).items().getFirst().lastActivityDate(),
                    question.items().getFirst().answerCount()
                );
            }
            case "github.com" -> {
                var repository = githubClient.fetchRepository(url).block();
                return jdbcLinksRepository.addRepository(
                    tgChatId,
                    url.toString(),
                    Objects.requireNonNull(repository).pushedAt(),
                    repository.subscribersCount()
                );
            }
            default -> {
                return jdbcLinksRepository.addLink(tgChatId, url.toString(), OffsetDateTime.now());
            }
        }
    }

    @Override
    public LinkModel remove(Long tgChatId, URI url) {
        return jdbcLinksRepository.removeLink(tgChatId, url.toString());
    }

    @Override
    public List<LinkResponse> findAll(Long tgChatId) {
        return jdbcLinksRepository.findLinksByChatId(tgChatId).stream()
            .map(linkModel -> new LinkResponse(
                URI.create(linkModel.link()),
                linkModel.lastUpdate()
            ))
            .collect(Collectors.toList());
    }
}

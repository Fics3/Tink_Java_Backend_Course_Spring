package edu.java.domain.repository.jpa;

import edu.java.domain.repository.GithubRepositoryRepository;
import edu.java.domain.repository.jpa.entitiesRepository.JpaChatEntityRepository;
import edu.java.domain.repository.jpa.entitiesRepository.JpaGithubRepositoryEntityRepository;
import edu.java.domain.repository.jpa.entitiesRepository.JpaLinkEntityRepository;
import edu.java.domain.repository.jpa.entity.GithubRepositoryEntity;
import edu.java.exception.BadRequestScrapperException;
import edu.java.model.GithubRepositoryModel;
import edu.java.model.LinkModel;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaGithubRepositoryRepository implements GithubRepositoryRepository {

    private final JpaGithubRepositoryEntityRepository jpaGithubRepositoryEntityRepository;
    private final JpaLinkEntityRepository jpaLinkEntityRepository;
    private final JpaChatEntityRepository jpaChatEntityRepository;

    @Override
    public GithubRepositoryModel getRepositoryByLinkId(UUID uuid) {
        var repository = jpaGithubRepositoryEntityRepository.findByLink(
            jpaLinkEntityRepository.findByLinkId(uuid)
        );
        return new GithubRepositoryModel(repository.getRepositoryId(), repository.getLink().getLinkId(),
            repository.getSubscribersCount()
        );
    }

    @Override
    public LinkModel addRepository(LinkModel linkModel, Integer subscribersCount) {
        var linkEntity = jpaLinkEntityRepository.findByLinkId(linkModel.linkId());
        jpaGithubRepositoryEntityRepository.save(
            new GithubRepositoryEntity(linkEntity, subscribersCount));
        return linkModel;
    }

    @Override
    public void updateSubscribersCount(UUID linkId, Integer subscribersCount) {
        jpaGithubRepositoryEntityRepository.updateSubscribersCount(linkId, subscribersCount);
    }

    @Override
    public void deleteRepository(Long tgChatId, String url) {
        var chat = jpaChatEntityRepository.findByTelegramChatId(tgChatId)
            .orElseThrow(() -> new BadRequestScrapperException("Пользователь не зарегестрирован", ""));
        var linkEntity = jpaLinkEntityRepository.findByLinkAndChatsContains(url, chat);
        var repo = jpaGithubRepositoryEntityRepository.findByLink(linkEntity);
        if (repo != null) {
            jpaGithubRepositoryEntityRepository.delete(repo);
        }
    }
}

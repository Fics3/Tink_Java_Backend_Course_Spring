package edu.java.domain.repository;

import edu.java.model.GithubRepositoryModel;
import edu.java.model.LinkModel;
import java.util.UUID;

public interface GithubRepositoryRepository {
    GithubRepositoryModel getRepositoryByLinkId(UUID uuid);

    LinkModel addRepository(LinkModel linkModel, Integer subscribersCount);

    void updateSubscribersCount(UUID linkId, Integer subscribersCount);

    void deleteRepository(Long tgChatId, String url);
}

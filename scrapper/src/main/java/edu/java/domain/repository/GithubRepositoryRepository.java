package edu.java.domain.repository;

import edu.java.model.GithubRepositoryModel;
import java.util.UUID;

public interface GithubRepositoryRepository {
    GithubRepositoryModel getRepositoryByLinkId(UUID uuid);

    void updateSubscribersCount(UUID linkId, Integer subscribersCount);
}

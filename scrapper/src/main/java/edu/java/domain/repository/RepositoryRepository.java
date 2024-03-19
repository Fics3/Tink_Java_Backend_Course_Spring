package edu.java.domain.repository;

import edu.java.model.RepositoryModel;
import java.util.UUID;

public interface RepositoryRepository {
    RepositoryModel getRepositoryByLinkId(UUID uuid);
}

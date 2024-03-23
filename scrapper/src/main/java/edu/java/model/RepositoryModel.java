package edu.java.model;

import java.util.UUID;

public record RepositoryModel(
    Integer repositoryId,
    UUID linkId,
    Integer subscribersCount
) {
}

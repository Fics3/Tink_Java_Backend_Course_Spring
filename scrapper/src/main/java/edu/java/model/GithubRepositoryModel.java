package edu.java.model;

import java.util.UUID;

public record GithubRepositoryModel(
    Integer repositoryId,
    UUID linkId,
    Integer subscribersCount
) {
}

package org.example.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.net.URI;
import java.time.OffsetDateTime;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public record GithubRepositoryResponse(
    String name,
    String fullName,
    String owner,
    String description,
    URI htmlURL,
    OffsetDateTime lastUpdateTime
) {
}

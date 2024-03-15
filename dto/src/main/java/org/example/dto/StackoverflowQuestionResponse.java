package org.example.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.net.URI;
import java.time.OffsetDateTime;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public record StackoverflowQuestionResponse(
    Long questionId,
    String title,
    URI link,
    OffsetDateTime lastActivityDate) {
}

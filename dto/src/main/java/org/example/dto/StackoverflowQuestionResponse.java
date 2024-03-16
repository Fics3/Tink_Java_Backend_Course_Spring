package org.example.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.OffsetDateTime;
import java.util.List;

public record StackoverflowQuestionResponse(List<ItemResponse> items) {

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public record ItemResponse(Integer questionId,
                               String title,
                               Boolean isAnswered,
                               OffsetDateTime lastActivityDate) {
    }
}

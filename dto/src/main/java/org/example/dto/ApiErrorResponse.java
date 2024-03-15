package org.example.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)

public record ApiErrorResponse
    (String description,
     String code,
     String exceptionName,
     String exceptionMessage,
     List<String> stacktrace) {
}

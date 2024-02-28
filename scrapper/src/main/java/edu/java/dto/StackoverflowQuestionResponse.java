package edu.java.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.Getter;

@Getter
public class StackoverflowQuestionResponse {
    @JsonProperty("question_id")
    private long questionId;

    private String title;

    private String link;

    @JsonProperty("last_activity_date")
    private OffsetDateTime lastActivityDate;

}

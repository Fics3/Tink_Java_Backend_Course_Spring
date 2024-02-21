package edu.java.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.Getter;

@Getter
public class QuestionResponse {
    @JsonProperty("question_id")
    private long questionId;

    private String title;

    private String link;

    @JsonProperty("last_activity_date")
    private OffsetDateTime lastActivityDate;

}

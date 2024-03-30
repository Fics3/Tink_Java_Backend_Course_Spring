package edu.java.model;

import java.util.UUID;

public record StackoverflowQuestionModel(Integer questionId, UUID linkId, Integer answerCount) {
}

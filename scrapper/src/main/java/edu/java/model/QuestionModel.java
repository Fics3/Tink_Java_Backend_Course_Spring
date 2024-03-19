package edu.java.model;

import java.util.UUID;

public record QuestionModel(Integer questionId, UUID linkId, Integer answerCount) {
}

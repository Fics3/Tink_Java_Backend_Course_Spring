package edu.java.domain.repository;

import edu.java.model.QuestionModel;
import java.util.UUID;

public interface QuestionRepository {

    QuestionModel getQuestionByLinkId(UUID uuid);
}

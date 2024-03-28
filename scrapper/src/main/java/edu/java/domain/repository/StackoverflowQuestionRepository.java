package edu.java.domain.repository;

import edu.java.model.StackoverflowQuestionModel;
import java.util.UUID;

public interface StackoverflowQuestionRepository {

    StackoverflowQuestionModel getQuestionByLinkId(UUID uuid);

    void updateAnswerCount(UUID linkId, Integer integer);

}

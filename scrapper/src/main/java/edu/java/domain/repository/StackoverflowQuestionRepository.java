package edu.java.domain.repository;

import edu.java.model.LinkModel;
import edu.java.model.StackoverflowQuestionModel;
import java.util.UUID;

public interface StackoverflowQuestionRepository {

    StackoverflowQuestionModel getQuestionByLinkId(UUID uuid);

    LinkModel addQuestion(LinkModel linkModel, Integer answerCount);

    void updateAnswerCount(UUID linkId, Integer integer);

}

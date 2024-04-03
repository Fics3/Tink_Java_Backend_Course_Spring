package edu.java.domain.repository.jpa;

import edu.java.domain.repository.StackoverflowQuestionRepository;
import edu.java.domain.repository.jpa.entitiesRepository.JpaChatEntityRepository;
import edu.java.domain.repository.jpa.entitiesRepository.JpaLinkEntityRepository;
import edu.java.domain.repository.jpa.entitiesRepository.JpaStackoverflowQuestionEntityRepository;
import edu.java.domain.repository.jpa.entity.StackoverflowQuestionEntity;
import edu.java.exception.BadRequestScrapperException;
import edu.java.model.LinkModel;
import edu.java.model.StackoverflowQuestionModel;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaStackoverflowQuestionRepository implements StackoverflowQuestionRepository {

    private final JpaStackoverflowQuestionEntityRepository jpaStackoverflowQuestionEntityRepository;
    private final JpaLinkEntityRepository jpaLinkEntityRepository;
    private final JpaChatEntityRepository jpaChatEntityRepository;

    @Override
    public StackoverflowQuestionModel getQuestionByLinkId(UUID uuid) {
        var question = jpaStackoverflowQuestionEntityRepository.findByLink(
            jpaLinkEntityRepository.findByLinkId(uuid)
        );
        return new StackoverflowQuestionModel(
            question.getQuestionId(),
            question.getLink().getLinkId(),
            question.getAnswerCount()
        );
    }

    @Override
    public LinkModel addQuestion(LinkModel linkModel, Integer answerCount) {
        var linkEntity = jpaLinkEntityRepository.findByLinkId(linkModel.linkId());
        jpaStackoverflowQuestionEntityRepository.save(
            new StackoverflowQuestionEntity(linkEntity, answerCount));
        return linkModel;
    }

    @Override
    public void updateAnswerCount(UUID linkId, Integer integer) {
        jpaStackoverflowQuestionEntityRepository.updateAnswerCount(linkId, integer);
    }

    @Override
    public void deleteQuestion(Long tgChatId, String url) {
        var chat = jpaChatEntityRepository.findByTelegramChatId(tgChatId)
            .orElseThrow(() -> new BadRequestScrapperException("Пользователь не зарегестрирован", ""));
        var linkEntity = jpaLinkEntityRepository.findByLinkAndChatsContains(url, chat);
        var question = jpaStackoverflowQuestionEntityRepository.findByLink(linkEntity);
        if (question != null) {
            jpaStackoverflowQuestionEntityRepository.delete(question);
        }
    }
}

package edu.java.domain.repository.jooq;

import edu.java.domain.repository.StackoverflowQuestionRepository;
import edu.java.model.LinkModel;
import edu.java.model.StackoverflowQuestionModel;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.domain.jooq.Tables.CHAT_LINK_RELATION;
import static edu.java.domain.jooq.Tables.LINKS;
import static edu.java.domain.jooq.Tables.QUESTIONS;

@Repository
@RequiredArgsConstructor
public class JooqStackoverflowQuestionRepository implements StackoverflowQuestionRepository {
    private final DSLContext dslContext;

    @Override
    public StackoverflowQuestionModel getQuestionByLinkId(UUID uuid) {
        return dslContext.selectFrom(QUESTIONS)
            .where(QUESTIONS.LINK_ID.eq(uuid))
            .fetchOneInto(StackoverflowQuestionModel.class);
    }

    @Override
    public LinkModel addQuestion(LinkModel linkModel, Integer answerCount) {
        dslContext.insertInto(QUESTIONS)
            .set(QUESTIONS.LINK_ID, linkModel.linkId())
            .set(QUESTIONS.ANSWER_COUNT, answerCount)
            .execute();

        return linkModel;
    }

    @Override
    public void updateAnswerCount(UUID linkId, Integer integer) {
        dslContext.update(QUESTIONS)
            .set(QUESTIONS.ANSWER_COUNT, integer)
            .where(QUESTIONS.LINK_ID.eq(linkId))
            .execute();
    }

    @Override
    public void deleteQuestion(Long tgChatId, String url) {
        UUID linkId = dslContext.select(LINKS.LINK_ID)
            .from(CHAT_LINK_RELATION.join(LINKS)
                .on(CHAT_LINK_RELATION.LINK_ID.eq(LINKS.LINK_ID)))
            .where(CHAT_LINK_RELATION.CHAT_ID.eq(tgChatId)
                .and(LINKS.LINK.eq(url)))
            .fetchOneInto(UUID.class);

        dslContext.deleteFrom(QUESTIONS)
            .where(QUESTIONS.LINK_ID.eq(linkId))
            .execute();
    }
}

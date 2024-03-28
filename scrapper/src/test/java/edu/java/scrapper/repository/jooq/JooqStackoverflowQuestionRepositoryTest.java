package edu.java.scrapper.repository.jooq;

import edu.java.domain.jooq.tables.records.QuestionsRecord;
import edu.java.domain.repository.jooq.JooqStackoverflowQuestionRepository;
import edu.java.model.StackoverflowQuestionModel;
import edu.java.scrapper.IntegrationTest;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.domain.jooq.Tables.LINKS;
import static edu.java.domain.jooq.Tables.QUESTIONS;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class JooqStackoverflowQuestionRepositoryTest extends IntegrationTest {

    @Autowired
    private JooqStackoverflowQuestionRepository jooqQuestionRepository;

    @Autowired
    private DSLContext dslContext;

    @Test
    void getQuestionByLinkIdTest() {
        // Arrange
        UUID linkId = UUID.randomUUID();
        int answerCount = 5;

        dslContext.insertInto(LINKS)
            .set(LINKS.LINK_ID, linkId)
            .set(LINKS.LINK, "http://example.com")
            .set(LINKS.LAST_CHECK, OffsetDateTime.now())
            .set(LINKS.LAST_UPDATE, OffsetDateTime.now())
            .execute();

        dslContext.insertInto(QUESTIONS)
            .set(QUESTIONS.LINK_ID, linkId)
            .set(QUESTIONS.QUESTION_ID, 1)
            .set(QUESTIONS.ANSWER_COUNT, answerCount)
            .execute();

        // Act
        StackoverflowQuestionModel stackoverflowQuestionModel = jooqQuestionRepository.getQuestionByLinkId(linkId);

        // Assert
        assertThat(stackoverflowQuestionModel).isNotNull();
        assertThat(stackoverflowQuestionModel.linkId()).isEqualTo(linkId);
        assertThat(stackoverflowQuestionModel.questionId()).isOne();
        assertThat(stackoverflowQuestionModel.answerCount()).isEqualTo(answerCount);
    }

    @Test
    void updateAnswerCountTest() {
        // Arrange
        UUID linkId = UUID.randomUUID();
        int initialAnswerCount = 5;
        dslContext.insertInto(LINKS)
            .set(LINKS.LINK_ID, linkId)
            .set(LINKS.LINK, "http://example.com")
            .set(LINKS.LAST_CHECK, OffsetDateTime.now())
            .set(LINKS.LAST_UPDATE, OffsetDateTime.now())
            .execute();

        dslContext.insertInto(QUESTIONS)
            .set(QUESTIONS.LINK_ID, linkId)
            .set(QUESTIONS.ANSWER_COUNT, initialAnswerCount)
            .execute();

        // Act
        int updatedAnswerCount = 10;
        jooqQuestionRepository.updateAnswerCount(linkId, updatedAnswerCount);

        // Assert
        Result<QuestionsRecord> result = dslContext.selectFrom(QUESTIONS)
            .where(QUESTIONS.LINK_ID.eq(linkId))
            .fetch();
        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().getValue(QUESTIONS.ANSWER_COUNT)).isEqualTo(updatedAnswerCount);
    }
}

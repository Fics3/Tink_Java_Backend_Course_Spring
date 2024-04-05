package edu.java.scrapper.repository.jpa;

import edu.java.domain.repository.jpa.entitesRepository.JpaLinkEntityRepository;
import edu.java.domain.repository.jpa.entitesRepository.JpaStackoverflowQuestionEntityRepository;
import edu.java.domain.repository.jpa.entity.ChatEntity;
import edu.java.domain.repository.jpa.entity.LinkEntity;
import edu.java.domain.repository.jpa.entity.StackoverflowQuestionEntity;
import edu.java.scrapper.IntegrationTest;
import jakarta.persistence.EntityManager;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class JpaStackoverflowQuestionEntityRepositoryTest extends IntegrationTest {

    @Autowired
    private JpaStackoverflowQuestionEntityRepository repository;
    @Autowired
    private JpaLinkEntityRepository jpaLinkEntityRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    @Rollback
    @Transactional
    void testUpdateAnswerCount() {
        // Arrange
        LinkEntity link = new LinkEntity(
            new ChatEntity(),
            UUID.randomUUID(),
            "http://example.com",
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
        jpaLinkEntityRepository.save(link);
        repository.save(new StackoverflowQuestionEntity(link, 0));
        entityManager.clear();
        // Act
        Integer newAnswerCount = 5;
        repository.updateAnswerCount(link.getLinkId(), newAnswerCount);

        // Assert
        StackoverflowQuestionEntity updatedQuestion = repository.findByLink(link);
        assertThat(updatedQuestion.getAnswerCount()).isEqualTo(newAnswerCount);
    }
}

package edu.java.scrapper.repository.jpa;

<<<<<<<< HEAD:scrapper/src/test/java/edu/java/scrapper/repository/jpa/JpaStackoverflowQuestionEntityRepositoryTest.java
import edu.java.domain.repository.jpa.entitiesRepository.JpaLinkEntityRepository;
import edu.java.domain.repository.jpa.entitiesRepository.JpaStackoverflowQuestionEntityRepository;
========
import edu.java.domain.repository.jpa.entitesRepository.JpaLinkEntityRepository;
import edu.java.domain.repository.jpa.entitesRepository.JpaStackoverflowQuestionEntityRepository;
>>>>>>>> ee69c78 (hw7 done):scrapper/src/test/java/edu/java/scrapper/repository/jpa/JpaStackoverflowStackoverflowQuestionEntityRepositoryTest.java
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
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
<<<<<<<< HEAD:scrapper/src/test/java/edu/java/scrapper/repository/jpa/JpaStackoverflowQuestionEntityRepositoryTest.java
@Transactional
public class JpaStackoverflowQuestionEntityRepositoryTest extends IntegrationTest {
========
public class JpaStackoverflowStackoverflowQuestionEntityRepositoryTest extends IntegrationTest {
>>>>>>>> ee69c78 (hw7 done):scrapper/src/test/java/edu/java/scrapper/repository/jpa/JpaStackoverflowStackoverflowQuestionEntityRepositoryTest.java

    @Autowired
    private JpaStackoverflowQuestionEntityRepository repository;
    @Autowired
    private JpaLinkEntityRepository jpaLinkEntityRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
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

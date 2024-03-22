package edu.java.scrapper.repository.jpa;

import edu.java.domain.entity.ChatEntity;
import edu.java.domain.entity.LinkEntity;
import edu.java.domain.entity.RepositoryEntity;
import edu.java.domain.repository.jpa.JpaRepositoryRepository;
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
public class JpaRepositoryRepositoryTest extends IntegrationTest {

    @Autowired
    private JpaRepositoryRepository repository;
    @Autowired
    private EntityManager entityManager;

    @Test
    @Rollback
    @Transactional
    void testUpdateSubscribersCount() {
        // Arrange
        LinkEntity link = new LinkEntity(
            new ChatEntity(),
            UUID.randomUUID(),
            "http://example.com",
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
        RepositoryEntity repositoryEntity = new RepositoryEntity(123, link, 213);
        repository.save(repositoryEntity);
        entityManager.clear();

        // Act
        Integer newSubscribersCount = 100;
        repository.updateSubscribersCount(link.getLinkId(), newSubscribersCount);

        // Assert
        RepositoryEntity updatedRepositoryEntity = repository.findByLink(link);
        assertThat(updatedRepositoryEntity.getSubscribersCount()).isEqualTo(newSubscribersCount);
    }
}

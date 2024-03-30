package edu.java.scrapper.repository.jpa;

import edu.java.domain.repository.jpa.entitesRepository.JpaGithubRepositoryEntityRepository;
import edu.java.domain.repository.jpa.entitesRepository.JpaLinkEntityRepository;
import edu.java.domain.repository.jpa.entity.ChatEntity;
import edu.java.domain.repository.jpa.entity.GithubRepositoryEntity;
import edu.java.domain.repository.jpa.entity.LinkEntity;
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
public class JpaGithubRepositoryEntityGithubRepositoryTest extends IntegrationTest {

    @Autowired
    private JpaGithubRepositoryEntityRepository jpaGithubRepositoryEntityRepository;
    @Autowired
    private JpaLinkEntityRepository jpaLinkEntityRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
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
        jpaLinkEntityRepository.save(link);
        GithubRepositoryEntity githubRepositoryEntity = new GithubRepositoryEntity(link, 213);
        jpaGithubRepositoryEntityRepository.save(githubRepositoryEntity);
        entityManager.clear();

        // Act
        Integer newSubscribersCount = 100;
        jpaGithubRepositoryEntityRepository.updateSubscribersCount(link.getLinkId(), newSubscribersCount);

        // Assert
        GithubRepositoryEntity updatedGithubRepositoryEntity = jpaGithubRepositoryEntityRepository.findByLink(link);
        assertThat(updatedGithubRepositoryEntity.getSubscribersCount()).isEqualTo(newSubscribersCount);
    }
}

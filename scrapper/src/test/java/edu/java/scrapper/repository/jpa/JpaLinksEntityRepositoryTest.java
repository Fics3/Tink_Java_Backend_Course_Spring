package edu.java.scrapper.repository.jpa;

<<<<<<<< HEAD:scrapper/src/test/java/edu/java/scrapper/repository/jpa/JpaLinksEntityRepositoryTest.java
<<<<<<<< HEAD:scrapper/src/test/java/edu/java/scrapper/repository/jpa/JpaLinksEntityRepositoryTest.java
import edu.java.domain.repository.jpa.entitiesRepository.JpaLinkEntityRepository;
========
import edu.java.domain.repository.jpa.entitesRepository.JpaLinkEntityRepository;
>>>>>>>> ee69c78 (hw7 done):scrapper/src/test/java/edu/java/scrapper/repository/jpa/JpaLinkEntityRepositoryTest.java
========
import edu.java.domain.repository.jpa.entitesRepository.JpaLinkEntityRepository;
>>>>>>>> origin/hw7:scrapper/src/test/java/edu/java/scrapper/repository/jpa/JpaLinkEntityRepositoryTest.java
import edu.java.domain.repository.jpa.entity.ChatEntity;
import edu.java.domain.repository.jpa.entity.LinkEntity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
<<<<<<<< HEAD:scrapper/src/test/java/edu/java/scrapper/repository/jpa/JpaLinksEntityRepositoryTest.java
<<<<<<<< HEAD:scrapper/src/test/java/edu/java/scrapper/repository/jpa/JpaLinksEntityRepositoryTest.java
@Transactional
class JpaLinksEntityRepositoryTest {
========
class JpaLinkEntityRepositoryTest {
>>>>>>>> ee69c78 (hw7 done):scrapper/src/test/java/edu/java/scrapper/repository/jpa/JpaLinkEntityRepositoryTest.java
========
class JpaLinkEntityRepositoryTest {
>>>>>>>> origin/hw7:scrapper/src/test/java/edu/java/scrapper/repository/jpa/JpaLinkEntityRepositoryTest.java

    @Autowired
    EntityManager entityManager;
    @Autowired
    private JpaLinkEntityRepository repository;

    @Test
    void testFindLinkEntityByLink() {
        // Arrange
        LinkEntity linkEntity = new LinkEntity(
            new ChatEntity(),
            UUID.randomUUID(),
            "http://example.com",
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
        repository.save(linkEntity);

        // Act
        Optional<LinkEntity> optionalLinkEntity = repository.findLinkEntityByLink("http://example.com");

        // Assert
        Assertions.assertTrue(optionalLinkEntity.isPresent());
        Assertions.assertEquals(linkEntity.getLink(), optionalLinkEntity.get().getLink());
    }

    @Test
    void testFindByLinkId() {
        // Arrange
        LinkEntity linkEntity = new LinkEntity(
            new ChatEntity(),
            UUID.randomUUID(),
            "http://example.com",
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        LinkEntity savedEntity = repository.save(linkEntity);

        // Act
        LinkEntity foundEntity = repository.findByLinkId(savedEntity.getLinkId());

        // Assert
        Assertions.assertNotNull(foundEntity);
        Assertions.assertEquals(savedEntity.getLinkId(), foundEntity.getLinkId());
    }

    @Test
    void testFindByLastCheckAfter() {
        // Arrange
        LinkEntity linkEntity1 = new LinkEntity(
            new ChatEntity(),
            UUID.randomUUID(),
            "http://example.com",
            OffsetDateTime.now(),
            OffsetDateTime.now().minusDays(1)
        );

        repository.save(linkEntity1);

        LinkEntity linkEntity2 = new LinkEntity(
            new ChatEntity(),
            UUID.randomUUID(),
            "http://example.com",
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        repository.save(linkEntity2);

        // Act
        List<LinkEntity> entities = repository.findByLastCheckAfter(OffsetDateTime.now().minusHours(1));

        // Assert
        Assertions.assertEquals(1, entities.size());
        Assertions.assertEquals(linkEntity2.getLink(), entities.getFirst().getLink());
    }

    @Test
    void testUpdateChecked() {
        // Arrange
        OffsetDateTime now = OffsetDateTime.now();
        LinkEntity linkEntity = new LinkEntity(
            new ChatEntity(),
            UUID.randomUUID(),
            "http://example.com",
            now,
            now
        );
        OffsetDateTime newLastCheck = now.plusDays(1);

        repository.save(linkEntity);

        // Act
        repository.updateChecked(linkEntity.getLinkId(), newLastCheck);
        entityManager.clear();

        // Assert
        LinkEntity updatedEntity = repository.findByLinkId(linkEntity.getLinkId());
        assertThat(updatedEntity.getLastCheck().getDayOfMonth()).isEqualTo(newLastCheck.getDayOfMonth());
    }

    @Test
    void testUpdateLastUpdate() {
        // Arrange
        LinkEntity linkEntity = new LinkEntity(
            new ChatEntity(),
            UUID.randomUUID(),
            "http://example.com",
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        repository.save(linkEntity);
        OffsetDateTime newLastUpdate = OffsetDateTime.now().plusDays(12);

        // Act
        repository.updateLastUpdate(linkEntity.getLinkId(), newLastUpdate);
        entityManager.clear();
        // Assert
        LinkEntity updatedEntity = repository.findByLinkId(linkEntity.getLinkId());
        assertThat(updatedEntity.getLastUpdate().getDayOfMonth()).isEqualTo(newLastUpdate.getDayOfMonth());
    }

}

package edu.java.scrapper.repository.jooq;

import edu.java.domain.jooq.tables.records.RepositoriesRecord;
import edu.java.domain.repository.jooq.JooqGithubRepositoryRepository;
import edu.java.model.GithubRepositoryModel;
import edu.java.scrapper.IntegrationTest;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.domain.jooq.Tables.LINKS;
import static edu.java.domain.jooq.Tables.REPOSITORIES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class JooqRepositoryRepositoryTest extends IntegrationTest {

    @Autowired
    private JooqGithubRepositoryRepository jooqGithubRepositoryRepository;

    @Autowired
    private DSLContext dslContext;

    @Test
    @Rollback
    @Transactional
    void getRepositoryByLinkIdTest() {
        // Arrange
        UUID linkId = UUID.randomUUID();
        int subscribersCount = 100;
        dslContext.insertInto(LINKS)
            .set(LINKS.LINK_ID, linkId)
            .set(LINKS.LINK, "http://example.com")
            .set(LINKS.LAST_CHECK, OffsetDateTime.now())
            .set(LINKS.LAST_UPDATE, OffsetDateTime.now())
            .execute();

        dslContext.insertInto(REPOSITORIES)
            .set(REPOSITORIES.LINK_ID, linkId)
            .set(REPOSITORIES.REPOSITORY_ID, 1)
            .set(REPOSITORIES.SUBSCRIBERS_COUNT, subscribersCount)
            .execute();

        // Act
        GithubRepositoryModel repositoryModel = jooqGithubRepositoryRepository.getRepositoryByLinkId(linkId);

        // Assert
        assertThat(repositoryModel).isNotNull();
        assertThat(repositoryModel.linkId()).isEqualTo(linkId);
        assertThat(repositoryModel.repositoryId()).isOne();
        assertThat(repositoryModel.subscribersCount()).isEqualTo(subscribersCount);
    }

    @Test
    @Rollback
    @Transactional
    void updateSubscribersCountTest() {
        // Arrange
        UUID linkId = UUID.randomUUID();
        int initialSubscribersCount = 100;
        dslContext.insertInto(LINKS)
            .set(LINKS.LINK_ID, linkId)
            .set(LINKS.LINK, "http://example.com")
            .set(LINKS.LAST_CHECK, OffsetDateTime.now())
            .set(LINKS.LAST_UPDATE, OffsetDateTime.now())
            .execute();

        dslContext.insertInto(REPOSITORIES)
            .set(REPOSITORIES.LINK_ID, linkId)
            .set(REPOSITORIES.SUBSCRIBERS_COUNT, initialSubscribersCount)
            .execute();

        // Act
        int updatedSubscribersCount = 150;
        jooqGithubRepositoryRepository.updateSubscribersCount(linkId, updatedSubscribersCount);

        // Assert
        Result<RepositoriesRecord> result = dslContext.selectFrom(REPOSITORIES)
            .where(REPOSITORIES.LINK_ID.eq(linkId))
            .fetch();
        assertThat(result).isNotEmpty();
        assertEquals(updatedSubscribersCount, result.getFirst().getValue(REPOSITORIES.SUBSCRIBERS_COUNT));
    }
}

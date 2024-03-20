package edu.java.domain.repository.jooq;

import edu.java.domain.repository.RepositoryRepository;
import edu.java.model.RepositoryModel;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.domain.jooq.Tables.REPOSITORIES;

@RequiredArgsConstructor
@Repository
public class JooqRepositoryRepository implements RepositoryRepository {
    private final DSLContext dslContext;

    @Override
    public RepositoryModel getRepositoryByLinkId(UUID uuid) {
        return dslContext.selectFrom(REPOSITORIES)
            .where(REPOSITORIES.LINK_ID.eq(uuid))
            .fetchOneInto(RepositoryModel.class);
    }

    @Override
    public void updateSubscribersCount(UUID linkId, Integer subscribersCount) {
        dslContext.update(REPOSITORIES)
            .set(REPOSITORIES.SUBSCRIBERS_COUNT, subscribersCount)
            .where(REPOSITORIES.LINK_ID.eq(linkId))
            .execute();
    }

}

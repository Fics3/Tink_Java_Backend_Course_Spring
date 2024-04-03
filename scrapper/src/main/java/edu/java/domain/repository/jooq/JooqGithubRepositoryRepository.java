package edu.java.domain.repository.jooq;

import edu.java.domain.repository.GithubRepositoryRepository;
import edu.java.model.GithubRepositoryModel;
import edu.java.model.LinkModel;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.domain.jooq.Tables.CHAT_LINK_RELATION;
import static edu.java.domain.jooq.Tables.LINKS;
import static edu.java.domain.jooq.Tables.REPOSITORIES;

@RequiredArgsConstructor
@Repository
public class JooqGithubRepositoryRepository implements GithubRepositoryRepository {
    private final DSLContext dslContext;

    @Override
    public GithubRepositoryModel getRepositoryByLinkId(UUID uuid) {
        return dslContext.selectFrom(REPOSITORIES)
            .where(REPOSITORIES.LINK_ID.eq(uuid))
            .fetchOneInto(GithubRepositoryModel.class);
    }

    @Override
    public LinkModel addRepository(LinkModel linkModel, Integer subscribersCount) {
        dslContext.insertInto(REPOSITORIES)
            .set(REPOSITORIES.LINK_ID, linkModel.linkId())
            .set(REPOSITORIES.SUBSCRIBERS_COUNT, subscribersCount)
            .execute();

        return linkModel;
    }

    @Override
    public void updateSubscribersCount(UUID linkId, Integer subscribersCount) {
        dslContext.update(REPOSITORIES)
            .set(REPOSITORIES.SUBSCRIBERS_COUNT, subscribersCount)
            .where(REPOSITORIES.LINK_ID.eq(linkId))
            .execute();
    }

    @Override
    public void deleteRepository(Long tgChatId, String url) {
        UUID linkId = dslContext.select(LINKS.LINK_ID)
            .from(CHAT_LINK_RELATION.join(LINKS)
                .on(CHAT_LINK_RELATION.LINK_ID.eq(LINKS.LINK_ID)))
            .where(CHAT_LINK_RELATION.CHAT_ID.eq(tgChatId)
                .and(LINKS.LINK.eq(url)))
            .fetchOneInto(UUID.class);

        dslContext.deleteFrom(REPOSITORIES)
            .where(REPOSITORIES.LINK_ID.eq(linkId))
            .execute();
    }

}

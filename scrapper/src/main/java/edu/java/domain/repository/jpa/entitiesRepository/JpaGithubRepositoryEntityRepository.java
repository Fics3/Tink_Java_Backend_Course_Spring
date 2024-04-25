package edu.java.domain.repository.jpa.entitiesRepository;

import edu.java.domain.repository.jpa.entity.GithubRepositoryEntity;
import edu.java.domain.repository.jpa.entity.LinkEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaGithubRepositoryEntityRepository extends JpaRepository<GithubRepositoryEntity, Integer> {
    GithubRepositoryEntity findByLink(LinkEntity linkEntity);

    @Modifying
    @Query("UPDATE GithubRepositoryEntity e set e.subscribersCount = :subscribersCount where e.link.linkId = :linkId")
    void updateSubscribersCount(@Param("linkId") UUID linkId, @Param("subscribersCount") Integer subscribersCount);
}

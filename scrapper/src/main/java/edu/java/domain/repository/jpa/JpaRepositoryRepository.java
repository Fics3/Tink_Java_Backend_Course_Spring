package edu.java.domain.repository.jpa;

import edu.java.domain.entity.LinkEntity;
import edu.java.domain.entity.RepositoryEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaRepositoryRepository extends JpaRepository<RepositoryEntity, Integer> {
    RepositoryEntity findByLink(LinkEntity linkEntity);

    @Modifying
    @Query("UPDATE RepositoryEntity e SET e.subscribersCount = :subscribersCount WHERE e.link.linkId = :linkId")
    void updateSubscribersCount(@Param("linkId") UUID linkId, @Param("subscribersCount") Integer subscribersCount);
}

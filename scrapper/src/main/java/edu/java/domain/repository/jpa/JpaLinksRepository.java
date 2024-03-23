package edu.java.domain.repository.jpa;

import edu.java.domain.entity.LinkEntity;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLinksRepository extends JpaRepository<LinkEntity, UUID> {

    Optional<LinkEntity> findLinkEntityByLink(String link);

    LinkEntity findByLinkId(UUID linkId);

    @Modifying
    @Query("FROM LinkEntity e WHERE e.lastCheck <= :lastCheck")
    List<LinkEntity> findByLastCheckAfter(@Param("lastCheck") OffsetDateTime lastCheck);

    @Transactional
    @Modifying
    @Query("UPDATE LinkEntity e SET e.lastCheck = :lastCheck WHERE e.linkId = :linkId")
    void updateChecked(@Param("linkId") UUID linkId, @Param("lastCheck") OffsetDateTime lastCheck);

    @Modifying
    @Query("UPDATE LinkEntity e SET e.lastUpdate = :lastUpdate WHERE e.linkId = :linkId")
    void updateLastUpdate(@Param("linkId") UUID linkId, @Param("lastUpdate") OffsetDateTime lastUpdate);

}

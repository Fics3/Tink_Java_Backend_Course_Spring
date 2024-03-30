package edu.java.domain.repository.jpa.entitesRepository;

import edu.java.domain.repository.jpa.entity.ChatEntity;
import edu.java.domain.repository.jpa.entity.LinkEntity;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLinkEntityRepository extends JpaRepository<LinkEntity, UUID> {

    Optional<LinkEntity> findLinkEntityByLink(String link);

    LinkEntity findByLinkId(UUID linkId);

    List<LinkEntity> findAllByChatsContains(ChatEntity chatEntity);

    LinkEntity findByLinkAndChatsContains(String link, ChatEntity chat);

    @Query("SELECT COUNT(l) > 0 FROM LinkEntity l JOIN l.chats c WHERE l.link = :link AND c IN :chatEntities")
    boolean existsByLinkAndChatEntities(
        @Param("link") String link,
        @Param("chatEntities") Set<ChatEntity> chatEntities
    );

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

package edu.java.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "links")
@Getter
@Setter
public class LinkEntity {

    @Id
    @Column(name = "link_id")
    private UUID linkId;

    @Column(name = "link")
    private String link;

    @Column(name = "last_update", nullable = false)
    private OffsetDateTime lastUpdate;

    @Column(name = "last_check", nullable = false)
    private OffsetDateTime lastCheck;

    @ManyToMany(mappedBy = "links")
    private Set<ChatEntity> chats = new HashSet<>();

    public LinkEntity(
        ChatEntity chatEntity,
        UUID linkId,
        String link,
        OffsetDateTime lastUpdate,
        OffsetDateTime lastCheck
    ) {
        chats.add(chatEntity);
        this.linkId = linkId;
        this.link = link;
        this.lastUpdate = lastUpdate;
        this.lastCheck = lastCheck;
    }

    public LinkEntity() {

    }
}

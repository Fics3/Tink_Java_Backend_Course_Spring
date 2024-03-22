package edu.java.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

@Entity
@Table(name = "chats")
@Getter
public class ChatEntity {

    @Id
    @Column(name = "telegram_chat_id")
    private Long telegramChatId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @ManyToMany
    @JoinTable(
        name = "chat_link_relation",
        joinColumns = @JoinColumn(name = "chat_id"),
        inverseJoinColumns = @JoinColumn(name = "link_id")
    )
    private Set<LinkEntity> links = new HashSet<>();

    public ChatEntity(Long telegramChatId, OffsetDateTime createdAt) {
        this.telegramChatId = telegramChatId;
        this.createdAt = createdAt;
    }

    public ChatEntity(Long telegramChatId, OffsetDateTime createdAt, Set<LinkEntity> links) {
        this.telegramChatId = telegramChatId;
        this.createdAt = createdAt;
        this.links = links;
    }

    public ChatEntity(Long telegramChatId) {
        this.telegramChatId = telegramChatId;
    }

    public ChatEntity() {

    }

}

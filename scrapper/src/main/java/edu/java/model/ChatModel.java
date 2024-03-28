package edu.java.model;

import java.time.OffsetDateTime;

public record ChatModel(Long telegramChatId, OffsetDateTime createdAt) {
}

package org.example.dto;

import java.net.URI;
import java.util.List;
import java.util.UUID;

public record LinkUpdateRequest(UUID id, URI url, String description, List<Long> tgChatIds) {
}

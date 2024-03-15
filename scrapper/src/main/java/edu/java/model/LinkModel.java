package edu.java.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LinkModel(UUID linkId,
                        String link,
                        OffsetDateTime lastUpdate,
                        OffsetDateTime checkedAt) {
}

package org.example.dto;

import java.net.URI;
import java.time.OffsetDateTime;

public record LinkResponse(URI url, OffsetDateTime lastUpdate) {
}

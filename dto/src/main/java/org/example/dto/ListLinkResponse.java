package org.example.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

public record ListLinkResponse(List<LinkResponse> links, Integer size) {
}

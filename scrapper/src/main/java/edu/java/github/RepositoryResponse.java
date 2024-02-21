package edu.java.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.Getter;

@Getter
public class RepositoryResponse {
    private String name;
    @JsonProperty("full_name")
    private String fullName;
    private String owner;
    private String description;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("created_at")
    private OffsetDateTime offsetDateTime;
}

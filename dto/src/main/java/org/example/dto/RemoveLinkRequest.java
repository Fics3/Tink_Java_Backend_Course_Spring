package org.example.dto;

import java.net.URI;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RemoveLinkRequest {
    private URI link;
}

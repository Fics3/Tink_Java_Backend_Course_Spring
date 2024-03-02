package org.example.dto;

import java.net.URI;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddLinkRequest {
    private URI uri;
}

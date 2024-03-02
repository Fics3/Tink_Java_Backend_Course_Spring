package org.example.dto;

import java.net.URI;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LinkResponse {
    private long id;
    private URI url;

}

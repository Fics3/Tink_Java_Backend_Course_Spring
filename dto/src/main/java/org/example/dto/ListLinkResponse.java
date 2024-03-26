package org.example.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ListLinkResponse {
    private List<LinkResponse> links;
    private int size;
}

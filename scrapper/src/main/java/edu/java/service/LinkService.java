package edu.java.service;

import edu.java.model.LinkModel;
import java.net.URI;
import java.util.List;
import org.example.dto.LinkResponse;

public interface LinkService {
    LinkModel add(Long tgChatId, URI url);

    LinkModel remove(Long tgChatId, URI url);

    List<LinkResponse> findAll(Long tgChatId);
}

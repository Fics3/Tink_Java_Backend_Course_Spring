package edu.java.service;

import edu.java.exception.DuplicateLinkScrapperException;
import edu.java.exception.NotFoundScrapperException;
import org.example.dto.AddLinkRequest;
import org.example.dto.RemoveLinkRequest;
import org.springframework.stereotype.Service;

@Service
public class LinkService {

    public void addLink(Long tgChatId, AddLinkRequest addLinkRequest) throws DuplicateLinkScrapperException {
        return;
    }

    public void removeLink(Long tgChatId, RemoveLinkRequest removeLinkRequest) throws NotFoundScrapperException {
        return;
    }
}

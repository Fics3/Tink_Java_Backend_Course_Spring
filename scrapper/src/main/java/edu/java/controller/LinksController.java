package edu.java.controller;

import java.util.ArrayList;
import java.util.List;
import org.example.dto.AddLinkRequest;
import org.example.dto.LinkResponse;
import org.example.dto.ListLinkResponse;
import org.example.dto.RemoveLinkRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/links")
public class LinksController {

    @GetMapping
    public ResponseEntity<Object> getLinks(@RequestHeader("Tg-Chat-Id") Long tgChatId) {
        List<LinkResponse> linkResponses = new ArrayList<>();

        int size = 0;

        ListLinkResponse listLinkResponse = new ListLinkResponse();
        listLinkResponse.setLinks(linkResponses);
        listLinkResponse.setSize(size);

        return ResponseEntity.ok(listLinkResponse);
    }

    @PostMapping
    public ResponseEntity<Object> addLink(
        @RequestHeader("Tg-Chat-Id") Long tgChatId,
        @RequestBody AddLinkRequest addLinkRequest
    ) {
        LinkResponse linkResponse = new LinkResponse();
        linkResponse.setId(1);
        linkResponse.setUrl(addLinkRequest.getUri());

        return ResponseEntity.ok(linkResponse);
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteLink(
        @RequestHeader("Tg-Chat-Id") Long tgChatId,
        @RequestBody RemoveLinkRequest removeLinkRequest
    ) {
        LinkResponse linkResponse = new LinkResponse();
        linkResponse.setId(tgChatId);
        linkResponse.setUrl(removeLinkRequest.getLink());

        return ResponseEntity.ok(linkResponse);
    }
}

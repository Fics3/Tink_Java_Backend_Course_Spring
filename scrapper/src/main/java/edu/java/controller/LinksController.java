package edu.java.controller;

import edu.java.service.LinkService;
import java.time.OffsetDateTime;
import java.util.List;
import org.example.dto.AddLinkRequest;
import org.example.dto.LinkResponse;
import org.example.dto.ListLinkResponse;
import org.example.dto.RemoveLinkRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private LinkService jooqLinkService;

    @GetMapping
    public ListLinkResponse getLinks(@RequestHeader("Tg-Chat-Id") Long tgChatId) {
        List<LinkResponse> linkResponses = jooqLinkService.findAll(tgChatId);
        return new ListLinkResponse(linkResponses, linkResponses.size());
    }

    @PostMapping
    public LinkResponse addLink(
        @RequestHeader("Tg-Chat-Id") Long tgChatId,
        @RequestBody AddLinkRequest addLinkRequest
    ) {
        jooqLinkService.add(tgChatId, addLinkRequest.uri());
        return new LinkResponse(addLinkRequest.uri(), OffsetDateTime.now());
    }

    @DeleteMapping
    public LinkResponse deleteLink(
        @RequestHeader("Tg-Chat-Id") Long tgChatId,
        @RequestBody RemoveLinkRequest removeLinkRequest
    ) {
        jooqLinkService.remove(tgChatId, removeLinkRequest.link());
        return new LinkResponse(removeLinkRequest.link(), OffsetDateTime.now());
    }
}

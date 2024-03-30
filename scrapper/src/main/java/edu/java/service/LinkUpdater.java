package edu.java.service;

import edu.java.domain.repository.LinksRepository;
import edu.java.service.updateChecker.UpdateChecker;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LinkUpdater {
    private final LinksRepository linksRepository;
    private final Map<String, UpdateChecker> updateCheckers;
    private final Duration threshold;

    public int update() {
        int updateCount = 0;
        var staleLinks = linksRepository.findStaleLinks(threshold);
        for (var linkModel : staleLinks) {
            updateCount += updateCheckers
                .get(URI.create(linkModel.link()).getHost())
                .processUrlUpdates(linkModel, updateCount);
            linksRepository.updateChecked(linkModel.linkId(), OffsetDateTime.now());
        }
        return updateCount;
    }
}

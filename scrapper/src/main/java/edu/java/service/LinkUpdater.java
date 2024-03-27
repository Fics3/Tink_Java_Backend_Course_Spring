package edu.java.service;

import edu.java.repository.LinksRepository;
import edu.java.service.updateChecker.UpdateChecker;
import java.net.URI;
import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LinkUpdater {
    private final LinksRepository linksRepository;
    private final Map<String, UpdateChecker> updateCheckers;
    @Value("#{@scheduler.forceCheckDelay().toMillis()}")
    private Duration threshold;

    public LinkUpdater(
        LinksRepository linksRepository,
        @Qualifier("updateCheckers") Map<String, UpdateChecker> updateCheckers
    ) {
        this.linksRepository = linksRepository;
        this.updateCheckers = updateCheckers;
    }

    public int update() {
        int updateCount = 0;
        var staleLinks = linksRepository.findStaleLinks(threshold);
        for (var linkModel : staleLinks) {
            updateCheckers
                .get(URI.create(linkModel.link()).getHost())
                .processUrlUpdates(linkModel, updateCount);
        }
        return updateCount;
    }
}

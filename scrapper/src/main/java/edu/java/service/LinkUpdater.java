package edu.java.service;

import edu.java.domain.repository.LinksRepository;
import edu.java.service.updateChecker.UpdateChecker;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LinkUpdater {
    private final LinksRepository jooqLinksRepository;
    private final Map<String, UpdateChecker> updateCheckers;
    private final Duration threshold;

    public LinkUpdater(
        LinksRepository jooqLinksRepository,
        @Qualifier("updateCheckers") Map<String, UpdateChecker> updateCheckers,
        @Value("#{@scheduler.forceCheckDelay().toMillis()}") Duration threshold
    ) {
        this.jooqLinksRepository = jooqLinksRepository;
        this.updateCheckers = updateCheckers;
        this.threshold = threshold;
    }

    public int update() {
        int updateCount = 0;
        var staleLinks = jooqLinksRepository.findStaleLinks(threshold);
        for (var linkModel : staleLinks) {
            updateCount += updateCheckers
                .get(URI.create(linkModel.link()).getHost())
                .processUrlUpdates(linkModel, updateCount);
            jooqLinksRepository.updateChecked(linkModel.linkId(), OffsetDateTime.now());
        }
        return updateCount;
    }
}

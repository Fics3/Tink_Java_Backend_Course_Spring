package edu.java.scrapper.service;

import edu.java.domain.repository.LinksRepository;
import edu.java.model.LinkModel;
import edu.java.service.LinkUpdater;
import edu.java.service.updateChecker.UpdateChecker;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class LinkUpdaterTest {

    @Mock
    private LinksRepository linksRepository;
    @Mock
    private UpdateChecker updateChecker;
    @InjectMocks
    private LinkUpdater linkUpdater;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Map<String, UpdateChecker> updateCheckers = new HashMap<>();
        updateCheckers.put("example.com", updateChecker);
        linkUpdater = new LinkUpdater(linksRepository, updateCheckers, Duration.ofHours(1));
    }

    @Test
    public void testUpdate() {
        // Given
        LinkModel linkModel = new LinkModel(
            UUID.randomUUID(),
            "https://example.com",
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
        when(linksRepository.findStaleLinks(any(Duration.class))).thenReturn(java.util.List.of(linkModel));
        when(updateChecker.processUrlUpdates(any(LinkModel.class), anyInt())).thenReturn(1);

        // When
        int updateCount = linkUpdater.update();

        // Then
        verify(updateChecker).processUrlUpdates(eq(linkModel), anyInt());
        assert updateCount == 1;
    }

    @Test
    public void testUpdateNoStaleLinks() {
        // Given
        when(linksRepository.findStaleLinks(any(Duration.class))).thenReturn(java.util.List.of());

        // When
        int updateCount = linkUpdater.update();

        // Then
        assert updateCount == 0;
        verifyNoInteractions(updateChecker);
    }
}

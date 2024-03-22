package edu.java.scrapper.service.jpa;

import edu.java.domain.entity.ChatEntity;
import edu.java.domain.entity.LinkEntity;
import edu.java.domain.repository.jpa.JpaChatRepository;
import edu.java.domain.repository.jpa.JpaLinksRepository;
import edu.java.exception.DuplicateLinkScrapperException;
import edu.java.model.LinkModel;
import edu.java.service.jpa.JpaLinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.example.dto.LinkResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class JpaLinkServiceTest {

    @Mock
    private JpaLinksRepository jpaLinksRepository;

    @Mock
    private JpaChatRepository jpaChatRepository;

    @InjectMocks
    private JpaLinkService linkService;

    @Test
    @DisplayName("should call save from repository")
    void testAdd() {
        // Arrange
        Long tgChatId = 123L;
        URI url = URI.create("http://example.com");
        OffsetDateTime now = OffsetDateTime.now();
        LinkEntity linkEntity = new LinkEntity(new ChatEntity(), UUID.randomUUID(), url.toString(), now, now);
        ChatEntity chatEntity = new ChatEntity(tgChatId, OffsetDateTime.now());
        when(jpaChatRepository.findByTelegramChatId(tgChatId)).thenReturn(Optional.of(chatEntity));
        when(jpaLinksRepository.findLinkEntityByLink(url.toString())).thenReturn(Optional.empty());
        when(jpaLinksRepository.save(any(LinkEntity.class))).thenReturn(linkEntity);

        // Act
        LinkModel result = linkService.add(tgChatId, url);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.link()).isEqualTo(url.toString());
    }

    @Test
    @DisplayName("should throw exception if link duplicated")
    void testAdd_HandleDuplicateLink() {
        // Arrange
        Long tgChatId = 123L;
        URI url = URI.create("http://example.com");
        when(jpaChatRepository.findByTelegramChatId(tgChatId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(DuplicateLinkScrapperException.class, () -> linkService.add(tgChatId, url));
    }

    @Test
    @DisplayName("should call delete from repo")
    void testRemove() {
        // Arrange
        Long tgChatId = 123L;
        URI url = URI.create("http://example.com");
        OffsetDateTime now = OffsetDateTime.now();
        ChatEntity chatEntity = new ChatEntity(tgChatId, OffsetDateTime.now());
        LinkEntity linkEntity = new LinkEntity(chatEntity, UUID.randomUUID(), url.toString(), now, now);
        jpaChatRepository.save(chatEntity);
        jpaLinksRepository.save(linkEntity);
        when(jpaChatRepository.findByTelegramChatId(tgChatId)).thenReturn(Optional.of(chatEntity));
        when(jpaLinksRepository.findLinkEntityByLink(url.toString())).thenReturn(Optional.of(linkEntity));

        // Act
        LinkModel result = linkService.remove(tgChatId, url);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.link()).isEqualTo(url.toString());
        assertThat(chatEntity.getLinks()).isEmpty();
        verify(jpaLinksRepository, times(1)).delete(linkEntity);
    }

    @Test
    @DisplayName("Should return list of one chat links")
    void testFindAllLinks() {
        // Arrange
        Long tgChatId = 123L;
        LinkEntity link1 = new LinkEntity(
            new ChatEntity(),
            UUID.randomUUID(),
            "http://example.com/1",
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
        LinkEntity link2 = new LinkEntity(
            new ChatEntity(),
            UUID.randomUUID(),
            "http://example.com/2",
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
        LinkEntity link3 = new LinkEntity(
            new ChatEntity(),
            UUID.randomUUID(),
            "http://example.com/3",
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
        ChatEntity chatEntity = new ChatEntity(tgChatId, OffsetDateTime.now(), Set.of(link3, link2, link1));

        when(jpaChatRepository.findByTelegramChatId(tgChatId)).thenReturn(Optional.of(chatEntity));

        // Act
        List<LinkResponse> links = linkService.findAll(tgChatId);

        // Assert
        assertThat(links).hasSize(3);
    }

}

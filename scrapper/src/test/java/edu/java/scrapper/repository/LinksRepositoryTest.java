package edu.java.scrapper.repository;

import edu.java.ScrapperApplication;
import edu.java.model.LinkModel;
import edu.java.repository.LinksRepository;
import edu.java.repository.mapper.LinkMapper;
import edu.java.scrapper.IntegrationTest;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ScrapperApplication.class)
public class LinksRepositoryTest extends IntegrationTest {
    @Autowired
    private LinksRepository linksRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    @Rollback
    void addLinkTest() {
        // Arrange
        String link = "test";
        jdbcTemplate.update("INSERT INTO chats VALUES (?, ?)", 123L, OffsetDateTime.now());

        // Act
        linksRepository.addLink(123L, link);

        // Assert
        List<LinkModel> links =
            jdbcTemplate.query("SELECT * FROM links WHERE link = ?", new LinkMapper(), link);
        assertThat(links).hasSize(1);
        assertThat(links.getFirst().link()).isEqualTo(link);
    }

    @Test
    @Transactional
    @Rollback
    void removeLinkTest() {
        // Arrange
        String link = "test";
        jdbcTemplate.update("INSERT INTO chats VALUES (?, ?)", 123L, OffsetDateTime.now());
        linksRepository.addLink(123L, link);

        // Act
        linksRepository.removeLink(123L, link);

        // Assert
        List<LinkModel> links =
            jdbcTemplate.query("SELECT * FROM links WHERE link = ?", new LinkMapper(), link);
        assertThat(links).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void findAllLinksTest() {
        // Arrange&Act
        var links = linksRepository.findAllLinks();

        // Assert
        assertThat(links).isNotNull();
    }
}

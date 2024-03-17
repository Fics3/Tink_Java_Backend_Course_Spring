package edu.java.scrapper.repository.jdbc;

import edu.java.domain.repository.jdbc.JdbcChatRepository;
import edu.java.domain.repository.jdbc.JdbcLinksRepository;
import edu.java.domain.repository.mapper.LinkMapper;
import edu.java.model.LinkModel;
<<<<<<<< HEAD:scrapper/src/test/java/edu/java/scrapper/repository/JdbcLinksRepositoryTest.java
import edu.java.repository.jdbc.JdbcChatRepository;
import edu.java.repository.jdbc.JdbcLinksRepository;
import edu.java.repository.mapper.LinkMapper;
========
>>>>>>>> 5d3111d (hw5 bonus):scrapper/src/test/java/edu/java/scrapper/repository/jdbc/JdbcLinksRepositoryTest.java
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

@SpringBootTest
<<<<<<<< HEAD:scrapper/src/test/java/edu/java/scrapper/repository/JdbcLinksRepositoryTest.java
@Transactional
@Rollback
========
>>>>>>>> 5d3111d (hw5 bonus):scrapper/src/test/java/edu/java/scrapper/repository/jdbc/JdbcLinksRepositoryTest.java
public class JdbcLinksRepositoryTest extends IntegrationTest {
    @Autowired
    private JdbcLinksRepository jdbcLinksRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private JdbcChatRepository jdbcChatRepository;

    @Test
    void addLinkTest() {
        // Arrange
        String link = "test";
        jdbcTemplate.update("INSERT INTO chats VALUES (?, ?)", 0L, OffsetDateTime.now());
        jdbcChatRepository.addChat(123L);

        // Act
        jdbcLinksRepository.addLink(123L, link, OffsetDateTime.now());

        // Assert
        List<LinkModel> links =
            jdbcTemplate.query("SELECT * FROM links WHERE link = ?", new LinkMapper(), link);
        assertThat(links).hasSize(1);
        assertThat(links.getFirst().link()).isEqualTo(link);
    }

    @Test
    void removeLinkTest() {
        // Arrange
        String link = "test";
        jdbcTemplate.update("INSERT INTO chats VALUES (?, ?)", 0L, OffsetDateTime.now());
        jdbcChatRepository.addChat(123L);
        jdbcLinksRepository.addLink(123L, link, OffsetDateTime.now());

        // Act
        jdbcLinksRepository.removeLink(123L, link);

        // Assert
        List<LinkModel> links =
            jdbcTemplate.query("SELECT * FROM links WHERE link = ?", new LinkMapper(), link);
        assertThat(links).isEmpty();
    }

    @Test
    void findAllLinksTest() {
        // Arrange&Act
        var links = jdbcLinksRepository.findAllLinks();

        // Assert
        assertThat(links).isNotNull();
    }
}

package edu.java.repository.jdbc;

import edu.java.model.LinkModel;
import edu.java.repository.LinksRepository;
import edu.java.repository.mapper.LinkMapper;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class JdbcLinksRepository implements LinksRepository {

    private final JdbcTemplate jdbcTemplate;

    public LinkModel addLink(Long tgChatId, String link, OffsetDateTime lastUpdate) {
        String sql = "INSERT INTO links (link_id, link, last_update, last_check) VALUES (?, ?, ?, ?)";
        UUID linkId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();

        jdbcTemplate.update(sql, linkId, link, lastUpdate, createdAt);

        String sqlRelation = "INSERT INTO chat_link_relation(chat_id, link_id)  VALUES (?, ?)";

        jdbcTemplate.update(sqlRelation, tgChatId, linkId);

        return new LinkModel(linkId, link, lastUpdate, createdAt);
    }

    public LinkModel removeLink(Long tgChatId, String link) {
        String sqlGetLinkId = "SELECT link_id FROM links WHERE link = ?";
        UUID linkId = jdbcTemplate.queryForObject(sqlGetLinkId, UUID.class, link);

        String sqlDeleteRelation = "DELETE FROM chat_link_relation WHERE chat_id = ? AND link_id = ?";
        jdbcTemplate.update(sqlDeleteRelation, tgChatId, linkId);

        String sqlDeleteLink = "DELETE FROM links WHERE link_id = ?";
        jdbcTemplate.update(sqlDeleteLink, linkId);

        return new LinkModel(linkId, link, null, null);
    }

    public List<LinkModel> findAllLinks() {
        String sql = "SELECT * FROM links";
        return jdbcTemplate.query(sql, new LinkMapper());
    }

    @Override
    public List<LinkModel> findLinksByChatId(Long tgChatId) {
        return null;
    }

    public boolean existsLinkForChat(Long tgChatId, String url) {
        String sql = "SELECT COUNT(*) FROM chat_link_relation clr "
            + "JOIN links l ON clr.link_id = l.link_id "
            + "WHERE clr.chat_id = ? AND l.link = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tgChatId, url);

        return count != null && count > 0;
    }

    public List<LinkModel> findStaleLinks(Duration threshold) {
        OffsetDateTime staleThreshold = OffsetDateTime.now().minus(threshold);

        String sql = "SELECT * FROM links WHERE last_check < ?";
        return jdbcTemplate.query(sql, new LinkMapper(), staleThreshold);
    }

    public void updateLastUpdate(UUID linkId, OffsetDateTime lastUpdate) {
        String sql = "UPDATE links SET last_update = ? WHERE link_id = ?";
        jdbcTemplate.update(sql, lastUpdate, linkId);
    }

    public void updateChecked(UUID linkId, OffsetDateTime checkedAt) {
        String sql = "UPDATE links SET last_check = ? WHERE link_id = ?";
        jdbcTemplate.update(sql, checkedAt, linkId);
    }

}

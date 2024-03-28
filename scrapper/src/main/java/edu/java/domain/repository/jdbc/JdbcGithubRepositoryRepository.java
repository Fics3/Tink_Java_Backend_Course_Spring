package edu.java.domain.repository.jdbc;

import edu.java.domain.repository.GithubRepositoryRepository;
import edu.java.domain.repository.jdbc.mapper.GithubRepositoryMapper;
import edu.java.model.GithubRepositoryModel;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class JdbcGithubRepositoryRepository implements GithubRepositoryRepository {
    private final JdbcTemplate jdbcTemplate;

    public GithubRepositoryModel getRepositoryByLinkId(UUID uuid) {
        String sql = "SELECT * FROM repositories WHERE link_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new GithubRepositoryMapper(), uuid);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void updateSubscribersCount(UUID linkId, Integer subscribersCount) {
        String sql = "UPDATE repositories set subscribers_count = ? where link_id = ?";
        jdbcTemplate.update(sql, subscribersCount, linkId);
    }
}

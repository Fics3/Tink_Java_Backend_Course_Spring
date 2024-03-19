package edu.java.domain.repository.jdbc;

import edu.java.domain.repository.mapper.QuestionMapper;
import edu.java.model.QuestionModel;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcQuestionRepository {
    private final JdbcTemplate jdbcTemplate;

    public QuestionModel getQuestionByLinkId(UUID uuid) {
        String sql = "SELECT * FROM questions WHERE link_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new QuestionMapper(), uuid);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}

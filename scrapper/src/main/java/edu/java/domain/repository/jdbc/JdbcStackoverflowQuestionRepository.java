package edu.java.domain.repository.jdbc;

import edu.java.domain.repository.StackoverflowQuestionRepository;
import edu.java.domain.repository.jdbc.mapper.StackoverflowQuestionMapper;
import edu.java.model.StackoverflowQuestionModel;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcStackoverflowQuestionRepository implements StackoverflowQuestionRepository {
    private final JdbcTemplate jdbcTemplate;

    public StackoverflowQuestionModel getQuestionByLinkId(UUID uuid) {
        String sql = "SELECT * FROM questions WHERE link_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new StackoverflowQuestionMapper(), uuid);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void updateAnswerCount(UUID linkId, Integer integer) {
        String sql = "UPDATE questions set answer_count = ? where link_id = ?";
        jdbcTemplate.update(sql, integer, linkId);
    }
}

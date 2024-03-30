package edu.java.domain.repository.jdbc;

import edu.java.domain.repository.StackoverflowQuestionRepository;
import edu.java.domain.repository.jdbc.mapper.StackoverflowQuestionMapper;
import edu.java.model.LinkModel;
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
    public LinkModel addQuestion(LinkModel linkModel, Integer answerCount) {
        String sqlRelation = "INSERT INTO questions(link_id, answer_count)  VALUES (?, ?)";

        jdbcTemplate.update(sqlRelation, linkModel.linkId(), answerCount);

        return linkModel;
    }

    @Override
    public void updateAnswerCount(UUID linkId, Integer integer) {
        String sql = "UPDATE questions set answer_count = ? where link_id = ?";
        jdbcTemplate.update(sql, integer, linkId);
    }

    @Override
    public void deleteQuestion(Long tgChatId, String url) {
        String sql = "SELECT links.link_id "
            + "FROM links "
            + "JOIN chat_link_relation ON links.link_id = chat_link_relation.link_id "
            + "WHERE chat_link_relation.chat_id = ? AND links.link = ?";

        UUID linkId = jdbcTemplate.queryForObject(sql, UUID.class, tgChatId, url);

        String sqlDelete = "DELETE FROM questions where link_id = ?";

        jdbcTemplate.update(sqlDelete, linkId);
    }
}

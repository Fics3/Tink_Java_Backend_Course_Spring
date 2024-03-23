package edu.java.domain.repository.mapper;

import edu.java.model.QuestionModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

public class QuestionMapper implements RowMapper<QuestionModel> {
    @Override
    public QuestionModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new QuestionModel(
            rs.findColumn("question_id"),
            UUID.fromString(rs.getString("link_id")),
            rs.getInt("answer_count")
        );
    }
}

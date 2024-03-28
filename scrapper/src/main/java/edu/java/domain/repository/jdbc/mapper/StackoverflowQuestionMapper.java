package edu.java.domain.repository.jdbc.mapper;

import edu.java.model.StackoverflowQuestionModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

public class StackoverflowQuestionMapper implements RowMapper<StackoverflowQuestionModel> {
    @Override
    public StackoverflowQuestionModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new StackoverflowQuestionModel(
            rs.findColumn("question_id"),
            UUID.fromString(rs.getString("link_id")),
            rs.getInt("answer_count")
        );
    }
}

package edu.java.repository.mapper;

import edu.java.model.ChatModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import static edu.java.util.DateUtil.getTimestampWithTimezone;

public class ChatMapper implements RowMapper<ChatModel> {

    @Override
    public ChatModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ChatModel(
            rs.getLong("telegram_chat_id"),
            getTimestampWithTimezone(rs.getTimestamp("created_at"))
        );
    }

}

<<<<<<<< HEAD:scrapper/src/main/java/edu/java/domain/repository/jdbc/mapper/ChatMapper.java
package edu.java.domain.repository.jdbc.mapper;
========
package edu.java.domain.repository.mapper;
>>>>>>>> 1b59763 (hw5 bonus):scrapper/src/main/java/edu/java/domain/repository/mapper/ChatMapper.java

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

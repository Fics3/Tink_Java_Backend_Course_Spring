package edu.java.repository;

import edu.java.model.ChatModel;
import edu.java.repository.mapper.ChatMapper;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ChatRepository {
    private final JdbcTemplate jdbcTemplate;

    public void addChat(Long chatId) {
        String sql = "INSERT INTO chats (telegram_chat_id, created_at) VALUES (?, ?)";
        jdbcTemplate.update(sql, chatId, OffsetDateTime.now());
    }

    public void removeChat(Long chatId) {
        String sqlDeleteRelations = "DELETE FROM chat_link_relation WHERE chat_id = ?";
        jdbcTemplate.update(sqlDeleteRelations, chatId);

        String sql = "DELETE FROM chats WHERE telegram_chat_id = ?";
        jdbcTemplate.update(sql, chatId);
    }

    public List<ChatModel> findAllChats() {
        String sql = "SELECT * from chats";
        return jdbcTemplate.query(sql, new ChatMapper());
    }

    public boolean existsChat(Long chatId) {
        String sql = "SELECT COUNT(*) FROM chats WHERE telegram_chat_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, chatId);
        return count != null && count > 0;
    }
}

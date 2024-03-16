package edu.java.scrapper.repository;

import edu.java.ScrapperApplication;
import edu.java.model.ChatModel;
import edu.java.repository.ChatRepository;
import edu.java.repository.mapper.ChatMapper;
import edu.java.scrapper.IntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ScrapperApplication.class)
public class ChatRepositoryTest extends IntegrationTest {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    @Rollback
    void addChatTest() {
        // Arrange
        Long chatId = 123456789L;

        // Act
        chatRepository.addChat(chatId);

        // Assert
        List<ChatModel> chats =
            jdbcTemplate.query("SELECT * FROM chats WHERE telegram_chat_id = ?", new ChatMapper(), chatId);
        assertThat(chats).hasSize(1);
        assertThat(chats.getFirst().telegramChatId()).isEqualTo(chatId);
    }

    @Test
    @Transactional
    @Rollback
    void removeChatTest() {
        // Arrange
        Long chatId = 123456789L;
        chatRepository.addChat(chatId);

        // Act
        chatRepository.removeChat(chatId);

        // Assert
        List<ChatModel> chats =
            jdbcTemplate.query("SELECT * FROM chats WHERE telegram_chat_id = ?", new ChatMapper(), chatId);
        assertThat(chats).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void findAllChatsTest() {
        // Arrange&Act
        var chats = chatRepository.findAllChats();

        // Assert
        assertThat(chats).isNotNull();
    }
}

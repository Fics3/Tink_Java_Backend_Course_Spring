package edu.java.scrapper.repository.jdbc;

import edu.java.domain.repository.jdbc.JdbcChatRepository;
import edu.java.domain.repository.jdbc.mapper.ChatMapper;
import edu.java.model.ChatModel;
<<<<<<<< HEAD:scrapper/src/test/java/edu/java/scrapper/repository/jdbc/JdbcChatRepositoryTest.java
========
import edu.java.repository.jdbc.JdbcChatRepository;
import edu.java.repository.mapper.ChatMapper;
>>>>>>>> 4951c65 (hw5 fixed):scrapper/src/test/java/edu/java/scrapper/repository/JdbcChatRepositoryTest.java
import edu.java.scrapper.IntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class JdbcChatRepositoryTest extends IntegrationTest {
    @Autowired
    private JdbcChatRepository jdbcChatRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void addChatTest() {
        // Arrange
        Long chatId = 123456789L;
        assertThat(POSTGRES.isRunning()).isTrue();

        // Act
        jdbcChatRepository.addChat(chatId);

        // Assert
        List<ChatModel> chats =
            jdbcTemplate.query("SELECT * FROM chats WHERE telegram_chat_id = ?", new ChatMapper(), chatId);
        assertThat(chats).hasSize(1);
        assertThat(chats.getFirst().telegramChatId()).isEqualTo(chatId);
    }

    @Test
    void removeChatTest() {
        // Arrange
        Long chatId = 123456789L;
        jdbcChatRepository.addChat(chatId);

        // Act
        jdbcChatRepository.removeChat(chatId);

        // Assert
        List<ChatModel> chats =
            jdbcTemplate.query("SELECT * FROM chats WHERE telegram_chat_id = ?", new ChatMapper(), chatId);
        assertThat(chats).isEmpty();
    }

    @Test
    void findAllChatsTest() {
        // Arrange&Act
        var chats = jdbcChatRepository.findAllChats();

        // Assert
        assertThat(chats).isNotNull();
    }
}

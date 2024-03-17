package edu.java.service.jdbc;

import edu.java.domain.repository.jdbc.JdbcChatRepository;
import edu.java.exception.DuplicateRegistrationScrapperException;
import edu.java.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JdbcChatService implements ChatService {

    @Autowired
    private JdbcChatRepository jdbcChatRepository;

    @Override
    public void add(Long tgChatId) {
        if (jdbcChatRepository.existsChat(tgChatId)) {
            throw new DuplicateRegistrationScrapperException(
                "Повторная регистрация",
                "Такой пользователь уже зарегистрирован"
            );
        }
        jdbcChatRepository.addChat(tgChatId);
    }

    @Override
    public void remove(Long tgChatId) {
        jdbcChatRepository.removeChat(tgChatId);
    }
}
